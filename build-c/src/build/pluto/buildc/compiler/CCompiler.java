/**
 * 
 */
package build.pluto.buildc.compiler;

import java.io.File;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * @author talen
 *
 */
public interface CCompiler extends Serializable {
	public static class CCompilerResult {
		private final Map<File, ? extends Collection<File>> generatedFiles;
		private final Collection<File> loadedFiles;

		public CCompilerResult(Map<File, ? extends Collection<File>> generatedFiles, Collection<File> loadedFiles) {
			this.generatedFiles = Collections.unmodifiableMap(generatedFiles);
			this.loadedFiles = Collections.unmodifiableCollection(loadedFiles);
		}
		public Map<File, ? extends Collection<File>> getGeneratedFiles() {
			return generatedFiles;
		}
		public Collection<File> getLoadedFiles() {
			return loadedFiles;
		}
	}

	public CCompilerResult compile(
			Collection<File> sourceFiles,
			File targetDir) throws Exception;
}
