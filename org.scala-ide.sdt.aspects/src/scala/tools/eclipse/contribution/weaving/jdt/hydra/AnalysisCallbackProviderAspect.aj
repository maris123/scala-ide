package scala.tools.eclipse.contribution.weaving.jdt.hydra;

import java.io.File;

import scala.collection.Set;
import xsbti.AnalysisCallback;
import xsbti.api.AnalyzedClass;
import xsbti.compile.IncOptions;
import xsbti.compile.Output;
import xsbti.compile.analysis.ReadStamps;

public aspect AnalysisCallbackProviderAspect {

	pointcut newAnalysisCallback(scala.Function1<String, scala.Option<String>> internalBinaryToSourceClassName, 
			scala.Function1<File, Set<String>> internalSourceToClassNamesMap, scala.Function2<File, String, scala.Option<AnalyzedClass>> externalAPI,
			ReadStamps stampReader, Output output, IncOptions options):
			execution(AnalysisCallback+.new(scala.Function1<String, scala.Option<String>>, 
					scala.Function1<File, Set<String>>, scala.Function2<File, String, scala.Option<AnalyzedClass>>,
					ReadStamps, Output, IncOptions)) &&
			args(internalBinaryToSourceClassName, internalSourceToClassNamesMap, externalAPI, stampReader, output, options);
			
	pointcut startSourcePointcut():
		call(* AnalysisCallback+.startSource(..));
	
	pointcut incrementalCompilePointcut():
		call(* IncrementalCompile.apply(..));
	
	pointcut buildInterfacePointcut():
		call(* CompilerBridgeStore.buildInterface(..));
		
	AnalysisCallback around(scala.Function1<String, scala.Option<String>> internalBinaryToSourceClassName, 
			scala.Function1<File, Set<String>> internalSourceToClassNamesMap, scala.Function2<File, String, scala.Option<AnalyzedClass>> externalAPI,
			ReadStamps stampReader, Output output, IncOptions options):
			newAnalysisCallback(internalBinaryToSourceClassName, internalSourceToClassNamesMap, externalAPI, stampReader, output, options) {
		AnalysisCallback analysisCallback = proceed(internalBinaryToSourceClassName, 
				internalSourceToClassNamesMap, externalAPI, stampReader, output, options);
		
		try {
			for (IAnalysisCallbackFactory factory : AnalysisCallbackProviderRegistry.getInstance().getProviders()) {
				if (factory.isHydraInstallation()) {
					AnalysisCallback hydraAnalysisCallback = factory.create(internalBinaryToSourceClassName, 
							internalSourceToClassNamesMap, externalAPI, stampReader, output, options);
					
					if (hydraAnalysisCallback != null) {
						return hydraAnalysisCallback;
					}
				}
			}
		} catch (Throwable t) {
			return analysisCallback;
		}
		
		return analysisCallback;
	}

	AnalysisCallback around():startSourcePointcut() {
		System.out.println("AICI");
		return null;
	}
}