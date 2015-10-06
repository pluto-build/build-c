package build.pluto.buildc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import build.pluto.test.build.TrackingBuildManager;





public class Test {

	public static void main(String[] args) {
		List<File> inputFiles = new ArrayList<>();
		String workingDir = System.getProperty("user.dir");
		inputFiles.add(new File(workingDir+"\\add.c"));
		CInput ci = new CInput(inputFiles,new File(workingDir+"\\bin"), inputFiles);
		
		try {
			//Exception when using TrackingBuilManag
			//TrackingBuildManager manager = new TrackingBuildManager();
			CBuilder builder = CBuilder.factory.makeBuilder(ci);
			
			//manager.require(CBuilder.request(ci));
			builder.build(ci);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
		}
		
	}

}
