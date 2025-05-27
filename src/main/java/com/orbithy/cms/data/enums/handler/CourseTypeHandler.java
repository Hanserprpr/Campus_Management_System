package com.orbithy.cms.data.enums.handler;

import com.orbithy.cms.data.enums.CourseType;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.*;

@MappedTypes(CourseType.class)
public class CourseTypeHandler extends BaseTypeHandler<CourseType> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, CourseType parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getLabel());
    }

    @Override
    public CourseType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return CourseType.fromLabel(rs.getString(columnName));
    }

    @Override
    public CourseType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return CourseType.fromLabel(rs.getString(columnIndex));
    }

    @Override
    public CourseType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return CourseType.fromLabel(cs.getString(columnIndex));
    }
}
