package com.zpmc.ai.service.tools;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * 检索问题工具
 *
 * @author songqiang
 * @date 2025-12-01 8:53
 */
@Slf4j
public class InterviewQuestionTool {


    /**
     * 从面试鸭网站获取关键词相关的面试题列表
     *
     * @param keyWord 搜索关键词（如"redis"、"java多线程"）
     * @return 面试题列表，若失败则返回错误信息
     */
    @Tool(name = "interviewQuestionSearch", value = """
            Retrieves relevant interview questions from mianshiya.com based on a keyword.
            Use this tool when the user asks for interview questions about specific technologies,
            programming concepts, or job-related topics. The input should be a clear search term.
            """
    )
    public String searchInterviewQuestions(@P(value = "the keyword to search") String keyWord) throws IOException {
        ArrayList<String> questions = new ArrayList<>();
        String encodedKeyword = URLEncoder.encode(keyWord, StandardCharsets.UTF_8);
        String url = "https://www.mianshiya.com/search/all?searchText=" + encodedKeyword;
        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0")
                .timeout(5000)
                .get();
        Elements elements = doc.select(".ant-table-cell > a");
        elements.forEach(element -> questions.add(element.text().trim()));
        return String.join("\n", questions);
    }
}
