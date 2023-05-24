package com.luv2code.springmvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luv2code.springmvc.controller.GradebookController;
import com.luv2code.springmvc.models.CollegeStudent;
import com.luv2code.springmvc.models.Grade;
import com.luv2code.springmvc.models.GradebookCollegeStudent;
import com.luv2code.springmvc.models.MathGrade;
import com.luv2code.springmvc.repo.HistoryGradeDao;
import com.luv2code.springmvc.repo.MathGradeDao;
import com.luv2code.springmvc.repo.ScienceGradeDao;
import com.luv2code.springmvc.repo.StudentDao;
import com.luv2code.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource("/application-test.properties")
@AutoConfigureMockMvc
@SpringBootTest
public class GradeBookControllerTest {

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private StudentAndGradeService studentAndGradeServiceMock;

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private MathGradeDao mathGradeDao;

    @Autowired
    private ScienceGradeDao scienceGradeDao;

    @Autowired
    private HistoryGradeDao historyGradeDao;

    @Autowired
    private StudentAndGradeService studentService;

    private static MockHttpServletRequest request;

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

    @BeforeAll
    public static void setup() {
        request = new MockHttpServletRequest();
        request.addParameter("firstname", "Chad");
        request.addParameter("lastname", "Darby");
        request.addParameter("emailAddress", "chad@example.com");
    }

    @BeforeEach
    public void setupDatabase() {
        jdbc.execute(sqlAddStudent);
        jdbc.execute(sqlAddMathGrade);
        jdbc.execute(sqlAddScienceGrade);
        jdbc.execute(sqlAddHistoryGrade);
        jdbc.execute(sqlAddHistoryGrade2);
    }

    @AfterEach
    public void setupAfterTransaction() {
        jdbc.execute(sqlDeleteStudent);
        jdbc.execute(sqlDeleteMathGrade);
        jdbc.execute(sqlDeleteScienceGrade);
        jdbc.execute(sqlDeleteHistoryGrade);
    }

    @Test
    public void getStudentsHttpRequest() throws Exception {

        CollegeStudent student1 = new GradebookCollegeStudent("Eric", "Roby", "eric.roby@example.com");
        CollegeStudent student2 = new GradebookCollegeStudent("Chad", "Darby", "chad@example.com");
        List<CollegeStudent> collegeStudentList = new ArrayList<>(Arrays.asList(student1, student2));
        when(studentAndGradeServiceMock.getGradeBook()).thenReturn(collegeStudentList);
        assertIterableEquals(collegeStudentList, studentAndGradeServiceMock.getGradeBook());
        verify(studentAndGradeServiceMock, times(1)).getGradeBook();
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().isOk()).andReturn();
        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "index");
    }

    @Test
    public void createStudentByHttpRequest2() throws Exception {
        HashMap<String, Object> map = new HashMap<>();
        map.put("firstname", "John");
        map.put("lastname", "Doe");
        map.put("emailAddress", "johndoe@example.com");
        ObjectMapper objectMapper = new ObjectMapper();
        String json = null;

        try {
            json = objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            // Handle the exception...
        }

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post("/")
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk()).andReturn();
        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "index");
    }

    @Test
    public void createStudentByHttpRequest() throws Exception {

        CollegeStudent student1 = new CollegeStudent(
                "Andy",
                "Lin",
                "andy@example.com");

        List<CollegeStudent> collegeStudentList = new ArrayList<>(Arrays.asList(student1));

        when(studentAndGradeServiceMock.getGradeBook()).thenReturn(collegeStudentList);

        assertIterableEquals(collegeStudentList, studentAndGradeServiceMock.getGradeBook());

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("firstname", request.getParameterValues("firstname"))
                        .param("lastname", request.getParameterValues("lastname"))
                        .param("emailAddress", request.getParameterValues("emailAddress")))
                .andExpect(status().isOk()).andReturn();
        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "index");

        CollegeStudent verifyStudent = studentDao
                .findByEmailAddress("chad@example.com");
        assertNotNull(verifyStudent, "student should be found");
    }

    @Test
    public void deleteStudentHttpRequest() throws Exception {
        assertTrue(studentDao.findById(1).isPresent(), "Should be present");

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .delete("/delete/student/{id}", 1))
                .andExpect(status().isOk())
                .andReturn();
        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "index");
        assertFalse(studentDao.findById(1).isPresent());
    }

    @Test
    public void deleteStudentErrorPage() throws Exception {
        assertFalse(studentDao.findById(2).isPresent(), "Student 2 should not be present");

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .delete("/delete/student/{id}", 2))
                .andExpect(status().isOk())
                .andReturn();
        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "error");
        assertFalse(studentDao.findById(2).isPresent());
    }

    @Test
    public void studentInfoRequest() throws Exception {
        assertTrue(studentDao.findById(1).isPresent());
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .get("/studentInformation/{id}", 1))
                .andExpect(status().isOk())
                .andReturn();
        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "studentInformation");
    }

    @Test
    public void studentInfoRequestDoesNotExist() throws Exception {
        assertFalse(studentDao.findById(2).isPresent());
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .get("/studentInformation/{id}", 2))
                .andExpect(status().isOk())
                .andReturn();
        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @Test
    public void createValidGradeHttpRequest() throws Exception {
        assertTrue(studentDao.findById(1).isPresent());
        GradebookCollegeStudent gradebookCollegeStudent = studentService.getStudentInfo(1);
        assertEquals(1, gradebookCollegeStudent.getStudentGrades().getMathGradeResults().size());

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post("/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("grade", "80.90")
                        .param("gradeType", "math")
                        .param("studentId", "1"))
                .andExpect(status().isOk()).andReturn();
        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "studentInformation");

        gradebookCollegeStudent = studentService.getStudentInfo(1);
        assertEquals(2, gradebookCollegeStudent.getStudentGrades().getMathGradeResults().size());
    }

    @Test
    public void createValidGradeHttpRequestStudentNotExist() throws Exception {
        assertFalse(studentDao.findById(2).isPresent());

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post("/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("grade", "80.90")
                        .param("gradeType", "math")
                        .param("studentId", "2"))
                .andExpect(status().isOk()).andReturn();
        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @Test
    public void createValidGradeHttpRequestInvalidGradeType() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("grade", "80.90")
                        .param("gradeType", "engineering")
                        .param("studentId", "1"))
                .andExpect(status().isOk()).andReturn();
        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @Test
    public void deleteValidGradeHttpRequest() throws Exception {
        Optional<MathGrade> mathGrade = mathGradeDao.findById(1);
        assertTrue(mathGrade.isPresent());

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders
                .get("/grades/{id}/{gradeType}",  1, "math"))
                .andExpect(status().isOk()).andReturn();
        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "studentInformation");

        mathGrade = mathGradeDao.findById(1);
        assertFalse(mathGrade.isPresent());
    }

    @Test
    public void deleteValidGradeHttpRequestIdDoesNotExist() throws Exception{
        Optional<MathGrade> mathGrade = mathGradeDao.findById(2);
        assertFalse(mathGrade.isPresent());

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/grades/{id}/{gradeType}",  2, "math"))
                .andExpect(status().isOk()).andReturn();
        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @Test
    public void deleteValidGradeHttpRequestWithInvalidSubject() throws Exception{
        Optional<MathGrade> mathGrade = mathGradeDao.findById(1);
        assertTrue(mathGrade.isPresent());

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/grades/{id}/{gradeType}",  1, "Engineering "))
                .andExpect(status().isOk()).andReturn();
        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "error");
    }
}
