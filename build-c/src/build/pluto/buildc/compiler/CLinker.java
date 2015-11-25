package build.pluto.buildc.compiler;

import java.io.File;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

public interface CLinker extends Serializable {
	public static class CLinkerResult {
		private final File generatedFile;
		private final Collection<File> loadedFiles;

		public CLinkerResult(File generatedFile, Collection<File> loadedFiles) {
			this.generatedFile = generatedFile;
			this.loadedFiles = Collections.unmodifiableCollection(loadedFiles);
		}
		public File getGeneratedFile() {
			return generatedFile;
		}
		public Collection<File> getLoadedFiles() {
			return loadedFiles;
		}
	}

	public CLinkerResult link(
			File targetDir) throws Exception;
}
