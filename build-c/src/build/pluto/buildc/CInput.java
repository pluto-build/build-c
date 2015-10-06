package build.pluto.buildc;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public class CInput implements Serializable {

	private static final long serialVersionUID = 6256336102992811823L;
	private final List<File> inputFiles;
	private final File targetDir;
	private final List<File> sourcePath;

	
	public CInput(List<File> inputFiles, File targetDir, List<File> sourcePath) {
		if (sourcePath == null || sourcePath.isEmpty()) {
			throw new IllegalArgumentException("Provide at least one source path!");
		}
		
		List<File> absoluteInputFiles = new ArrayList<>(inputFiles.size());
		for (File f : inputFiles)
			absoluteInputFiles.add(f.getAbsoluteFile());
		this.inputFiles = Collections.unmodifiableList(absoluteInputFiles);
		this.targetDir = (targetDir != null ? targetDir : new File(".")).getAbsoluteFile();
		this.sourcePath = sourcePath;
		
	}

	public CInput(File inputFile, File targetDir, File sourcePath) {
		this(Collections.singletonList(inputFile), targetDir, Collections.singletonList(sourcePath));
	}
	public List<File> getInputFiles() {
		return inputFiles;
	}

	public File getTargetDir() {
		return targetDir;
	}

	public List<File> getSourcePath() {
		return sourcePath;
	}

	@Override
	public String toString() {
		return "CInput [inputFiles=" + inputFiles + ", targetDir=" + targetDir + ", sourcePath=" + sourcePath
				 + "]";
	}
	
	
	
	
}
