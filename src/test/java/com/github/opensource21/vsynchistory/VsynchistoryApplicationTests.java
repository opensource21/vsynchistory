package com.github.opensource21.vsynchistory;

import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = VsynchistoryApplication.class)
public class VsynchistoryApplicationTests {

    @BeforeClass
    public static void setUp() {
        // At TRAVIS there is no collection-data.
        final boolean travis = Boolean.parseBoolean(System.getenv("TRAVIS"));
        Assume.assumeFalse("Don't run at TRAVIS", travis);
    }
    
    @Test
    public void contextLoads() {
    }

}
