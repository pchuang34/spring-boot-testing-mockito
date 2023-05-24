package com.luv2code.test;

import com.luv2code.component.MvcTestingExampleApplication;
import com.luv2code.component.dao.ApplicationDao;
import com.luv2code.component.models.CollegeStudent;
import com.luv2code.component.models.StudentGrades;
import com.luv2code.component.service.ApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = MvcTestingExampleApplication.class)
public class ReflectionTest {
    @Autowired
    CollegeStudent collegeStudent1;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    StudentGrades studentGrades;

    @BeforeEach
    public void studentBeforeEach(){
        collegeStudent1.setFirstname("Eric");
        collegeStudent1.setLastname("Roby");
        collegeStudent1.setEmailAddress("ericRoby@example.com");
        collegeStudent1.setStudentGrades(studentGrades);

        ReflectionTestUtils.setField(collegeStudent1, "id", 1 );
        ReflectionTestUtils.setField(collegeStudent1, "studentGrades", new StudentGrades(
                new ArrayList<>(Arrays.asList(100.0, 85.0, 76.50, 91.75))
        ));
    }

    @Test
    void getPrivate(){
        assertEquals(1, ReflectionTestUtils.getField(collegeStudent1, "id"));  
    }


}
