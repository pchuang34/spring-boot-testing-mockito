package com.luv2code.test;

import com.luv2code.component.MvcTestingExampleApplication;
import com.luv2code.component.dao.ApplicationDao;
import com.luv2code.component.models.CollegeStudent;
import com.luv2code.component.models.StudentGrades;
import com.luv2code.component.service.ApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = MvcTestingExampleApplication.class)
public class MockAnnotationTest {

    @Autowired
    ApplicationContext context;

    @Autowired
    CollegeStudent collegeStudent1;

    @Autowired
    StudentGrades studentGrades;

    //  @Mock
    @MockBean
    private ApplicationDao applicationDao;

    // @InjectMocks
    @Autowired
    private ApplicationService applicationService;

    @BeforeEach
    public void beforeEach(){
        collegeStudent1.setFirstname("Eric");
        collegeStudent1.setLastname("Roby");
        collegeStudent1.setEmailAddress("ericRoby@example.com");
        collegeStudent1.setStudentGrades(studentGrades);
    }

    @Test
    void testAssertEquals(){
        when(applicationDao.addGradeResultsForSingleClass(
                studentGrades.getMathGradeResults())).thenReturn(100.00);
        assertEquals(100, applicationService.addGradeResultsForSingleClass(studentGrades.getMathGradeResults()));
        verify(applicationDao, times(1)).addGradeResultsForSingleClass(studentGrades.getMathGradeResults());
    }

    @Test
    @DisplayName("Find GPA")
    void testGPA(){
        when(applicationDao.findGradePointAverage(
                studentGrades.getMathGradeResults())).thenReturn(88.31);
        assertEquals(88.31, applicationService.findGradePointAverage(
                studentGrades.getMathGradeResults()
        ));
    }

    @DisplayName("Throw Runtime Error")
    @Test
    void testThrowRuntimeError(){
        CollegeStudent nullStudent = context.getBean(CollegeStudent.class);
        doThrow(new RuntimeException()).when(applicationDao).checkNull(nullStudent);

        assertThrows(RuntimeException.class, () -> {
            applicationService.checkNull(nullStudent);
        });

        verify(applicationDao, times(1)).checkNull(nullStudent);
    }

    @DisplayName("Multiple")
    @Test
    void testMulti(){
        //CollegeStudent nullStudent = (CollegeStudent) context.getBean("collegeStudent");
        CollegeStudent nullStudent = context.getBean(CollegeStudent.class);
        when(applicationDao.checkNull(nullStudent))
                .thenThrow(new RuntimeException())
                .thenReturn("Second Time");
        assertThrows(RuntimeException.class, () -> {
            applicationService.checkNull(nullStudent);
        });
        assertEquals("Second Time", applicationService.checkNull(nullStudent));

        verify(applicationDao,times(2)).checkNull(nullStudent);
    }

}
