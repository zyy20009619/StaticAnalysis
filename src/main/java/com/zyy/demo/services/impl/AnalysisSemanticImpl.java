package com.zyy.demo.services.impl;

import com.zyy.demo.entities.FileSimilarity;
import com.zyy.demo.entities.SingelCollect;
import com.zyy.demo.entities.AtomicFloat;
import com.zyy.demo.services.IAnalysisSemanticFile;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class AnalysisSemanticImpl implements IAnalysisSemanticFile {
    DealSCAndAPIFile dealSCAndAPIFile = new DealSCAndAPIFile();

    public void analysisSemanticFile(String basepath) {
        //1.获取到项目下所有.java文件的路径
        List<String> classpathes = getAllJavaFile(basepath);
        //2.获取标识符
        List<List<String>> identifierListAllFile = getIdenfitier(classpathes);
        //3.词干提取
        List<List<String>> textStem = stemExtraction(identifierListAllFile);
        //4.获取语义相似性
        List<FileSimilarity> fileSimilarities = getSimilarity(textStem);
        //5.输出语义相似性文件
        outputSimilarityFile(basepath, fileSimilarities, classpathes);
    }

    private void outputSimilarityFile(String basepath, List<FileSimilarity> fileSimilarities, List<String> classpathes) {
        String concerndepPath = basepath + File.separator + "concerndep.csv";

        File concerndepFile = new File(concerndepPath);
        try {
            concerndepFile.createNewFile();
            FileWriter fileWriter = new FileWriter(concerndepFile);
            for (FileSimilarity fileSimilarity : fileSimilarities) {
                fileWriter.write(classpathes.get(Integer.parseInt(fileSimilarity.getFile1Index())) + "," + classpathes.get(Integer.parseInt(fileSimilarity.getFile2Index())) + "," + fileSimilarity.getSimilarityScore() + "\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> getAllJavaFile(String basepath) {
        dealSCAndAPIFile.readAllPaths(basepath, "senman");
        List<String> classpathes = new ArrayList<>();
        SingelCollect.getService2class().forEach((service, classpaths) -> {
            classpaths.forEach(classpath -> {
                String name = classpath.replace(".", "\\");
                classpathes.add(basepath + File.separator + service + File.separator + "src\\main\\java" + File.separator + name + ".java");
            });
        });
//        System.out.println(classpathes);
        return classpathes;
    }

    private List<List<String>> getIdenfitier(List<String> classpathes) {
        List<List<String>> identifierListAllFile = new ArrayList<List<String>>();
        List<String> tempClassPaths = new ArrayList<>();
        tempClassPaths.addAll(classpathes);
        classpathes.forEach(classpath -> {
            SingelCollect.clearIdentifierListOneFile();
            ClassParser.visitIdenfitier(new File(classpath).getPath());
            if (SingelCollect.getIdentifierListOneFile().size() == 0) {
                tempClassPaths.remove(classpath);
            } else {
                List<String> tempIdentifier = new ArrayList<>();
                tempIdentifier.addAll(SingelCollect.getIdentifierListOneFile());
                identifierListAllFile.add(tempIdentifier);
            }
        });

        return identifierListAllFile;
    }

    private List<FileSimilarity> getSimilarity(List<List<String>> textStem) {
        List<FileSimilarity> fileSimilarities = new ArrayList<>();
        for (int i = 0; i < textStem.size(); i++) {
            for (int j = i + 1; j < textStem.size(); j++) {
                if (i != j) {
                    fileSimilarities.add(new FileSimilarity(Integer.toString(i), Integer.toString(j), getSimilarity(textStem.get(i), textStem.get(j))));
                }
            }
        }
        return fileSimilarities;
    }

    private double getSimilarity(List<String> text1, List<String> text2) {
        double score = getSimilarityImpl(text1, text2);

        score = (int) (score * 1000000 + 0.5) / (double) 1000000;
        return score;
    }

    private double getSimilarityImpl(List<String> text1, List<String> text2) {
        // 1.词频统计（key是词，value是该词在这段句子中出现的次数）
        Map<String, Integer> frequency1 = getFrequency(text1);
        Map<String, Integer> frequency2 = getFrequency(text2);

        // 2.将所有词都装入set容器
        Set<String> words = new HashSet<>();
        words.addAll(text1);
        words.addAll(text2);

        AtomicFloat ab = new AtomicFloat();// a.b
        AtomicFloat aa = new AtomicFloat();// |a|的平方
        AtomicFloat bb = new AtomicFloat();// |b|的平方

        //3.写出词频向量，之后再进行相似度计算
        words.forEach(word -> {
            //计算同一词在a、b两个集合出现的次数
            Integer x1 = frequency1.get(word);
            Integer x2 = frequency2.get(word);
            if (x1 != null && x2 != null) {
                //x1x2
                float oneOfTheDimension = x1 * x2;
                //+
                ab.addAndGet(oneOfTheDimension);
            }
            if (x1 != null) {
                //(x1)^2
                float oneOfTheDimension = x1 * x1;
                //+
                aa.addAndGet(oneOfTheDimension);
            }
            if (x2 != null) {
                //(x2)^2
                float oneOfTheDimension = x2 * x2;
                //+
                bb.addAndGet(oneOfTheDimension);
            }
        });

        //|a| 对aa开方
        double aaa = Math.sqrt(aa.doubleValue());
        //|b| 对bb开方
        double bbb = Math.sqrt(bb.doubleValue());

        //使用BigDecimal保证精确计算浮点数
        BigDecimal aabb = BigDecimal.valueOf(aaa).multiply(BigDecimal.valueOf(bbb));

        //similarity=a.b/|a|*|b|
        //divide参数说明：aabb被除数,9表示小数点后保留9位，最后一个表示用标准的四舍五入法
        double cos = BigDecimal.valueOf(ab.get()).divide(aabb, 9, BigDecimal.ROUND_HALF_UP).doubleValue();
        return cos;
    }

    private Map<String, Integer> getFrequency(List<String> text) {
        Map<String, AtomicInteger> freq = new HashMap<>();
        Map<String, Integer> result = new HashMap<>();
        text.forEach(word -> freq.computeIfAbsent(word, k -> new AtomicInteger()).incrementAndGet());
        for (Map.Entry<String, AtomicInteger> entry: freq.entrySet()) {
            result.put(entry.getKey(), Integer.parseInt(entry.getValue().toString()));
        }
        return result;
    }

    private List<List<String>> stemExtraction(List<List<String>> identifierListAllFile) {
        List<List<String>> result = new ArrayList<>();
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        identifierListAllFile.forEach(identifierListOne -> {
            List<String> temp = new ArrayList<String>();
            identifierListOne.forEach(text -> {
                Annotation document = new Annotation(text);
                pipeline.annotate(document);
                List<CoreMap> words = document.get(CoreAnnotations.SentencesAnnotation.class);
                for(CoreMap word_temp: words) {
                    for (CoreLabel token: word_temp.get(CoreAnnotations.TokensAnnotation.class)) {
//                        String word = token.get(CoreAnnotations.TextAnnotation.class);   // 获取单词信息
                        String lema = token.get(CoreAnnotations.LemmaAnnotation.class);  // 获取对应上面word的词元信息，即我所需要的词形还原后的单词
//                        System.out.println(word + " " + lema);
                        temp.add(lema);
                    }
                }
            });
            result.add(temp);
        });
        return result;
    }
}
