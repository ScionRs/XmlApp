package ru.ravilov.PasrseXMLApp.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;
import ru.ravilov.PasrseXMLApp.model.Employee;
import ru.ravilov.PasrseXMLApp.service.AppRunner;
import ru.ravilov.PasrseXMLApp.service.CustomerRowMapper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Класс взаоимодействия с базой данных
 */

@Repository
public class EmployeeRepository {

    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public EmployeeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Employee employee){
        String SQL = "INSERT INTO employee(depCode, depJob, description) VALUES (?,?,?)";
        jdbcTemplate.update(SQL, employee.getDepCode(), employee.getDepJob(), employee.getDescription());
        System.out.printf("Добавление записи %s,%s,%s \n", employee.getDepCode(),employee.getDepJob(),employee.getDescription());
        AppRunner.log.info("Добавление записи в БД завершено");
    }

    public List<Employee> findAll(){
        String sql = "SELECT depCode,depJob,description FROM employee";
        List<Employee> employees = jdbcTemplate.query(
                sql,
                new CustomerRowMapper());
                return employees;
    }
    public void deleteByDepJob(Employee employee){
        String sql = "DELETE FROM employee WHERE depJob=?";
        jdbcTemplate.update(sql,employee.getDepJob());
        AppRunner.log.info("Удаление записи в БД завершено");
    }
    public void updateByList(Integer employee){
        String sql = "UPDATE FROM employee where id = ?";
        jdbcTemplate.update(sql,employee);
        AppRunner.log.info("Обновление записи в БД завершено");
    }
}

