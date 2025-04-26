package com.orbithy.cms.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orbithy.cms.data.po.Term;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JsonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // 获取 term.json 的绝对路径
    private static String getTermJsonPath() {
        String basePath = new File("").getAbsolutePath();
        return basePath + File.separator + "data" + File.separator + "term.json";
    }

    // 确保 data 目录和 term.json 文件存在
    private static void ensureTermJsonExists() throws IOException {
        File dataDir = new File(new File("").getAbsolutePath(), "data");
        if (!dataDir.exists()) {
            dataDir.mkdirs(); // 创建 data 目录
        }

        File termFile = new File(getTermJsonPath());
        if (!termFile.exists()) {
            // 如果 term.json 不存在，创建并写入一个空数组 []
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(termFile, new ArrayList<>());
        }
    }

    // 读取 term.json
    public static List<Term> readTerms() throws IOException {
        ensureTermJsonExists(); // 确保文件存在
        return objectMapper.readValue(new File(getTermJsonPath()), new TypeReference<List<Term>>() {});
    }

    // 写入 term.json
    public static void writeTerms(List<Term> terms) throws IOException {
        ensureTermJsonExists(); // 确保文件存在
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(getTermJsonPath()), terms);
    }

    public static boolean termExists(String termName) throws IOException {
        List<Term> terms = readTerms();
        return terms.stream()
                .anyMatch(t -> t.getTerm().equals(termName));
    }

    // 如果还想顺便拿到 Term 对象，可以这样写：
    public static Optional<Term> findTerm(String termName) throws IOException {
        List<Term> terms = readTerms();
        return terms.stream()
                .filter(t -> t.getTerm().equals(termName))
                .findFirst();
    }
}
