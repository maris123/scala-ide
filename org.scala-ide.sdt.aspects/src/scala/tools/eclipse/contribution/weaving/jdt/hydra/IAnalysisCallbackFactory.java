package scala.tools.eclipse.contribution.weaving.jdt.hydra;

import java.io.File;

import scala.Option;
import scala.collection.Set;
import xsbti.AnalysisCallback;
import xsbti.api.AnalyzedClass;
import xsbti.compile.IncOptions;
import xsbti.compile.Output;
import xsbti.compile.analysis.ReadStamps;

public interface IAnalysisCallbackFactory {
	public AnalysisCallback create(scala.Function1<String, Option<String>> internalBinaryToSourceClassName, 
			scala.Function1<File, Set<String>> internalSourceToClassNamesMap, scala.Function2<File, String, Option<AnalyzedClass>> externalAPI,
			ReadStamps stampReader, Output output, IncOptions options);
	public boolean isHydraInstallation();
}
