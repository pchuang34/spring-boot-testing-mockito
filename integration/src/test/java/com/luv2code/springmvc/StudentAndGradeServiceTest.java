package com.luv2code.springmvc;

import com.luv2code.springmvc.models.*;
import com.luv2code.springmvc.repo.HistoryGradeDao;
import com.luv2code.springmvc.repo.MathGradeDao;
import com.luv2code.springmvc.repo.ScienceGradeDao;
import com.luv2code.springmvc.repo.StudentDao;
import com.luv2code.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource("/application-test.properties")
@SpringBootTest
public class StudentAndGradeServiceTest {

    @Autowired
    JdbcTemplate jdbc;

    @Autowired
    StudentAndGradeService studentService;

    @Autowired
    StudentDao studentDao;

    @Autowired
    MathGradeDao mathGradeDao;

    @Autowired
    ScienceGradeDao scienceGradeDao;

    @Autowired
    HistoryGradeDao historyGradeDao;

    @Value("${sql.scripts.create.student}")
    private String sqlAddStudent;
    @Value("${sql.scripts.create.math.grade}")
    private String sqlAddMathGrade;
    @Value("${sql.scripts.create.science.grade}")
    private String sqlAddScienceGrade;
    @Value("${sql.scripts.create.history.grade}")
    private String sqlAddHistoryGrade;
    @Value("${sql.scripts.create.history.grade2}")
    private String sqlAddHistoryGrade2;

    @Value("${sql.scripts.delete.student}")
    private String sqlDeleteStudent;
    @Value("${sql.scripts.delete.math}")
    private String sqlDeleteMathGrade;
    @Value("${sql.scripts.delete.science}")
    private String sqlDeleteScienceGrade;
    @Value("${sql.scripts.delete.history}")
    private String sqlDeleteHistoryGrade;

    @BeforeEach
    public void setupDatabase() {
        jdbc.execute(sqlAddStudent);
        jdbc.execute(sqlAddMathGrade);
        jdbc.execute(sqlAddScienceGrade);
        jdbc.execute(sqlAddHistoryGrade);
        jdbc.execute(sqlAddHistoryGrade2);
    }

    @AfterEach
    public void setupAfterTransaction(){
        jdbc.execute(sqlDeleteStudent);
        jdbc.execute(sqlDeleteMathGrade);
        jdbc.execute(sqlDeleteScienceGrade);
        jdbc.execute(sqlDeleteHistoryGrade);
    }

    @Test
    public void createStudentService(){
        studentService.createStudent("Chad", "Darby", "chad@example.com");
        CollegeStudent student = studentDao
                .findByEmailAddress("chad@example.com");
        assertEquals("chad@example.com", student.getEmailAddress(), "find by email");
    }

    @Test
    public void  isStudentNullCheck(){
        assertTrue(studentService.isStudentExist(1), "Should have a student with id 1");
        assertFalse(studentService.isStudentExist(0), "Shouldn't have a student with id 0");
    }

    @Test
    public void deleteStudentService(){
        Optional<CollegeStudent> student = studentDao.findById(1);
        Optional<MathGrade> mathGrade = mathGradeDao.findById(1);
        Optional<ScienceGrade> scienceGrade = scienceGradeDao.findById(1);
        Optional<HistoryGrade> historyGrade = historyGradeDao.findById(1);
        Optional<HistoryGrade> historyGrade2 = historyGradeDao.findById(2);

        assertTrue(student.isPresent(), "Student 1 should exist");
        assertTrue(mathGrade.isPresent());
        assertTrue(scienceGrade.isPresent());
        assertTrue(historyGrade.isPresent());
        assertTrue(historyGrade2.isPresent());

        studentService.deleteStudent(1);

        student = studentDao.findById(1);
        mathGrade = mathGradeDao.findById(1);
        scienceGrade = scienceGradeDao.findById(1);
        historyGrade = historyGradeDao.findById(1);
        historyGrade2 = historyGradeDao.findById(2);

        assertFalse(student.isPresent(), "Student 1 should not exist");
        assertFalse(mathGrade.isPresent());
        assertFalse(scienceGrade.isPresent());
        assertFalse(historyGrade.isPresent());
        assertFalse(historyGrade2.isPresent());
    }

    @Sql("/insertData.sql")
    @Test
    public void getGradeBookService(){
        Iterable<CollegeStudent> collegeStudentIterable = studentService.getGradeBook();
        List<CollegeStudent> collegeStudents = new ArrayList<>();
        for(CollegeStudent student : collegeStudentIterable){
            collegeStudents.add(student);
        }
        assertEquals(5, collegeStudents.size());
    }

    @Test
    public void createGradeService(){
        //create the grade
        assertTrue(studentService.createGrade(80.5, 1, "math"));
        assertTrue(studentService.createGrade(80.8, 1, "science"));
        assertTrue(studentService.createGrade(80.9, 1, "history"));

        //get all grade with student ID
        Iterable<MathGrade> mathGrades = mathGradeDao.findGradeByStudentId(1);
        Iterable<ScienceGrade> scienceGrades = scienceGradeDao.findGradeByStudentId(1);
        Iterable<HistoryGrade> historyGrades = historyGradeDao.findGradeByStudentId(1);

        //verify there is a grade
        assertTrue(((Collection<MathGrade>) mathGrades).size() == 2);
        assertTrue(((Collection<ScienceGrade>) scienceGrades).size() == 2);
        assertTrue(((Collection<HistoryGrade>) historyGrades).size() == 3);
    }

    @Test
    public void createGradeServiceReturnFalse(){
        assertFalse(studentService.createGrade(105, 1, "math"));
        assertFalse(studentService.createGrade(-5, 1, "math"));
        assertFalse(studentService.createGrade(80, 2, "math"));
        assertFalse(studentService.createGrade(80, 1, "engineering"));
    }

    @Test
    public void deleteGradeService(){
        //delete grade
        assertEquals(1, studentService.deleteGrade(1, "math"));

        //verify there is no grade with id 1
        assertFalse(mathGradeDao.findById(1).isPresent());
    }

    @Test
    public void deleteGradeServiceReturnNegativeId(){
        assertEquals(-1, studentService.deleteGrade(0, "math"));
        assertEquals(-1, studentService.deleteGrade(1, "engineering"));
    }

    @Test
    public void studentInformation(){
        GradebookCollegeStudent gradebookCollegeStudent = studentService.getStudentInfo(1);

        assertNotNull(gradebookCollegeStudent);
        assertEquals(1, gradebookCollegeStudent.getId());
        assertEquals("Eric", gradebookCollegeStudent.getFirstname());
        assertEquals("Roby", gradebookCollegeStudent.getLastname());
        assertEquals("eric.roby@example.com", gradebookCollegeStudent.getEmailAddress());

        assertTrue(gradebookCollegeStudent.getStudentGrades()
                .getMathGradeResults().size() == 1);

        assertTrue(gradebookCollegeStudent.getStudentGrades()
                .getScienceGradeResults().size() == 1);

        assertTrue(gradebookCollegeStudent.getStudentGrades()
                .getHistoryGradeResults().size() == 2);
    }

    @Test
    public void studentInfoReturnNull(){
        assertNull(studentService.getStudentInfo(3));
    }
}
