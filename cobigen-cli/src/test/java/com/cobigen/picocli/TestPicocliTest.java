package com.cobigen.picocli;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Scanner;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestPicocliTest {
	private static Logger logger = LoggerFactory.getLogger(TestPicocliTest.class);
	/** Test resources root path */
    private static String testFileRootPath = "src/test/resources/testdata/unittest/";
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testMain() {
		try {
			 File baseFile = new File(testFileRootPath + "EmployeeEntity.java");
			 
			String args[]=new String[1] ;
				args[0]= baseFile.getAbsolutePath();
				
			TestPicocli.main(args[0]);
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			fail(e.getMessage());			
		}
	}

}