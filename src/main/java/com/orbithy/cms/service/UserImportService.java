package com.orbithy.cms.service;
import com.orbithy.cms.data.enums.Major;
import com.orbithy.cms.data.po.Status;
import com.orbithy.cms.data.po.User;
import com.orbithy.cms.mapper.StatusMapper;
import com.orbithy.cms.mapper.UserMapper;
import com.orbithy.cms.utils.BcryptUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserImportService {

    private final UserMapper userMapper;
    private final StatusMapper statusMapper;

    public UserImportService(UserMapper userMapper, StatusMapper statusMapper) {
        this.userMapper = userMapper;
        this.statusMapper = statusMapper;
    }

    @Transactional
    public void importUsersFromExcel(MultipartFile file) throws Exception {
        InputStream inputStream = file.getInputStream();
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        List<User> users = new ArrayList<>();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) { // 跳过标题行
            Row row = sheet.getRow(i);
            if (row == null) continue;

            User user = new User();
            user.setUsername(getCellValue(row.getCell(0)));
            user.setSex(getCellValue(row.getCell(1)));
            user.setEmail(getCellValue(row.getCell(2)));
            user.setPhone(getCellValue(row.getCell(3)));
            user.setPassword(BcryptUtils.encrypt(getCellValue(row.getCell(4))));
            user.setSDUId(getCellValue(row.getCell(5)));
            Integer code = parseInteger(row.getCell(6));
            Major majorEnum = Major.fromCode(code);
            user.setMajor(majorEnum);
            Byte permission = parseByte(row.getCell(7));
            if (permission != null && permission == 0) {
                throw new IllegalArgumentException("第 " + (i + 1) + " 行的权限(permission)不能为0！");
            }
            user.setPermission(permission);
            user.setNation(getCellValue(row.getCell(8)));
            user.setEthnic(getCellValue(row.getCell(9)));
            user.setPoliticsStatus(getCellValue(row.getCell(10)));
            user.setCollege(getCellValue(row.getCell(11)));
            users.add(user);

        }

        if (!users.isEmpty()) {
            userMapper.insertBatch(users);
            List<Status> statusList = users.stream()
                    .map(user -> {
                        if (user.getId() == null) throw new RuntimeException("User ID is null");
                        Integer grade = Integer.valueOf(user.getSDUId().substring(0, 4));
                        Status status = new Status();
                        status.setId(user.getId());
                        status.setGrade(grade);
                        status.setAdmission(grade);
                        status.setGraduation(grade + 4);
                        return status;
                    })
                    .toList();

            statusMapper.insertBatch(statusList);
        }

        workbook.close();
    }



    private String getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }

        FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();

        switch (cell.getCellType()) {
            case FORMULA:
                CellValue evaluatedValue = evaluator.evaluate(cell);
                if (evaluatedValue == null) {
                    return null;
                }
                switch (evaluatedValue.getCellType()) {
                    case STRING:
                        return evaluatedValue.getStringValue().trim();
                    case NUMERIC:
                        double num = evaluatedValue.getNumberValue();
                        if (Math.floor(num) == num) {
                            return String.valueOf((long) num); // 整数，不带小数点
                        } else {
                            return String.valueOf(num);
                        }
                    case BOOLEAN:
                        return String.valueOf(evaluatedValue.getBooleanValue());
                    default:
                        return null;
                }
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                double num = cell.getNumericCellValue();
                if (Math.floor(num) == num) {
                    return String.valueOf((long) num);
                } else {
                    return String.valueOf(num);
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return null;
        }
    }


    private Integer parseInteger(Cell cell) {
        try {
            return Integer.parseInt(getCellValue(cell));
        } catch (Exception e) {
            return null;
        }
    }

    private Byte parseByte(Cell cell) {
        try {
            return Byte.parseByte(getCellValue(cell));
        } catch (Exception e) {
            return null;
        }
    }
}
