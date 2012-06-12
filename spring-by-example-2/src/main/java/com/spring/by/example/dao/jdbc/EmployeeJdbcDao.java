package com.spring.by.example.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import com.spring.by.example.domain.Employee;

@Repository
public class EmployeeJdbcDao extends JdbcDaoSupport {

	@Resource
	public void initDataSource(DataSource dataSource) {
		setDataSource(dataSource);
	}

	public Employee find(Long id) {
		final Employee result = new Employee();
		getJdbcTemplate().query("select * from test_db.employee where id = ?",
				new Object[] { id }, new RowCallbackHandler() {
					public void processRow(ResultSet rs) throws SQLException {
						result.setId(Long.valueOf(rs.getString("id")));
						result.setFirstName(rs.getString("firstName"));
						result.setLastName(rs.getString("lastName"));
						result.setProject(rs.getString("project"));
					}
				});
		final Set<Employee> matesSet = new HashSet<Employee>();
		getJdbcTemplate().query("select e.* from test_db.project_mates as pm inner join test_db.employee as e on (pm.employee2 = e.id) where pm.employee1 = ?",
				new Object[] { id }, new RowCallbackHandler() {
					public void processRow(ResultSet rs) throws SQLException {
						Employee mate = new Employee();
						mate.setId(Long.valueOf(rs.getString("id")));
						mate.setFirstName(rs.getString("firstName"));
						mate.setLastName(rs.getString("lastName"));
						mate.setProject(rs.getString("project"));
						matesSet.add(mate);
					}
				});
		result.setProjectMates(matesSet);
		return result;
	}

	public void save(final Employee employee) {
		getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection conn)
					throws SQLException {
				String sql = "insert into test_db.employee (firstName, lastName, project) values (?, ?, ?)";
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, employee.getFirstName());
				ps.setString(2, employee.getLastName());
				ps.setString(3, employee.getProject());
				return ps;
			}
		});
		insertEmployeeMates(employee);
	}

	private void insertEmployeeMates(final Employee employee) {
		for (final Employee mate : employee.getProjectMates()) {
			insertMatePair(employee, mate);
			insertMatePair(mate, employee);
		}
	}

	private void insertMatePair(final Employee mate1, final Employee mate2) {
		getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection conn)
					throws SQLException {
				String sql = "insert into test_db.project_mates (employee1, employee2) values (?, ?)";
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, mate1.getId().toString());
				ps.setString(2, mate2.getId().toString());
				return ps;
			}
		});
	}

	public void update(final Employee employee) {
		getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection conn)
					throws SQLException {
				String sql = "update test_db.employee set firstName = ?, lastName = ?, project = ? where id = ?";
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, employee.getFirstName());
				ps.setString(2, employee.getLastName());
				ps.setString(3, employee.getProject());
				ps.setString(4, employee.getId().toString());
				return ps;
			}
		});
		deleteEmployeeMates(employee.getId());
		insertEmployeeMates(employee);
	}

	private void deleteEmployeeMates(Long employeeId) {
		getJdbcTemplate().execute(
				"delete from test_db.project_mates where employee1 = " + employeeId + " or employee2 = " + employeeId);
	}

	public void delete(Long id) {
		deleteEmployeeMates(id);
		getJdbcTemplate().execute(
				"delete from test_db.employee where id = " + id);
	}

}
