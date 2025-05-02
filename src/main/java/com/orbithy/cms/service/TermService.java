package com.orbithy.cms.service;

import com.orbithy.cms.data.po.Term;
import com.orbithy.cms.data.vo.Result;
import com.orbithy.cms.utils.JsonUtil;
import com.orbithy.cms.utils.ResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class TermService {
    /**
     * 添加学期
     *
     * @param term 学期信息
     * @return ResponseEntity<Result>
     */
    public ResponseEntity<Result> addTerm(String term) throws IOException {
        List<Term> terms = JsonUtil.readTerms();
        if (term == null || term.trim().isEmpty()) {
            return ResponseUtil.build(Result.error(400, "学期名不能为空"));
        }

        if (JsonUtil.termExists(term)) {
            ;
            return ResponseUtil.build(Result.error(400, "学期已存在"));
        }
        Term termObj = new Term();
        termObj.setTerm(term);
        terms.add(termObj);
        JsonUtil.writeTerms(terms);
        return ResponseUtil.build(Result.ok());
    }

    /**
     * 修改选课开放
     *
     * @param term 学期信息
     * @return ResponseEntity<Result>
     */
    public ResponseEntity<Result> updateTerm(Term term) throws IOException {
            if (term == null || term.getTerm() == null || term.getTerm().trim().isEmpty()) {
                return ResponseUtil.build(Result.error(400, "学期名不能为空"));
            }
        if (!term.getTerm().matches("\\d{4}-\\d{4}-[12]")) {
            return ResponseUtil.build(Result.error(400, "无效的学期格式"));
        }

            List<Term> terms = JsonUtil.readTerms();

            boolean found = false;
            for (Term t : terms) {
                if (t.getTerm().equals(term.getTerm())) {
                    found = true;
                    // 如果想要修改成 open
                    if (term.isOpen()) {
                        // 检查有没有其他 open 的学期
                        boolean alreadyHasOpen = terms.stream()
                                .anyMatch(x -> !x.getTerm().equals(t.getTerm()) && x.isOpen());
                        if (alreadyHasOpen) {
                            return ResponseUtil.build(Result.error(400, "已有开放学期，不能同时开放多个"));
                        }
                    }
                    // 更新 isOpen 状态
                    t.setOpen(term.isOpen());
                    break;
                }
            }

            if (!found) {
                return ResponseUtil.build(Result.error(404, "学期不存在"));
            }

            JsonUtil.writeTerms(terms);
            return ResponseUtil.build(Result.ok());

    }



    /**
     * 删除学期
     *
     * @param termName 学期
     * @return ResponseEntity<Result>
     */
    public ResponseEntity<Result> deleteTerm(String termName) throws IOException {
        List<Term> terms = JsonUtil.readTerms();
        // 根据 term 名字过滤，保留不是目标 term 的
        List<Term> updatedTerms = terms.stream()
                .filter(t -> !t.getTerm().equals(termName))
                .toList();
        if (updatedTerms.size() == terms.size()) {
            // 没删掉，说明没有找到要删的 term
            return ResponseUtil.build(Result.error(404, "学期不存在"));
        }
        // 写回
        JsonUtil.writeTerms(updatedTerms);
        return ResponseUtil.build(Result.ok());
    }

    /**
     * 获取当前学期
     *
     * @return ResponseEntity<Result>
     */
    public static String getCurrentTerm() throws IOException {
        List<Term> terms = JsonUtil.readTerms();
        Term currentTerm = terms.stream()
                .filter(Term::isOpen)
                .findFirst()
                .orElse(null);
        if (currentTerm == null) {
            return null;
        }
        return currentTerm.getTerm();
    }

    public static boolean isTermOpen(String termName) throws IOException {
        List<Term> terms = JsonUtil.readTerms();
        return terms.stream()
                .filter(t -> t.getTerm().equals(termName))
                .anyMatch(Term::isOpen);
    }

    public ResponseEntity<Result> getTerms() throws IOException {
        List<Term> terms = JsonUtil.readTerms();
        return ResponseUtil.build(Result.success(terms, "获取学期列表成功"));
    }

}
