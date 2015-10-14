package build.pluto.buildc.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import build.pluto.buildc.test.simple.SimpleCBuildTest;

@RunWith(Suite.class)
@SuiteClasses({SimpleCBuildTest.class})
public class BuildCTestSuit {

}
