package scala.tools.eclipse.contribution.weaving.jdt.hydra;

import scala.tools.eclipse.contribution.weaving.jdt.util.AbstractProviderRegistry;

public class AnalysisCallbackProviderRegistry extends AbstractProviderRegistry<IAnalysisCallbackFactory> {

	private static final AnalysisCallbackProviderRegistry INSTANCE = new AnalysisCallbackProviderRegistry();

    public static String ANALYSIS_EXTENSION_POINT = "org.scala-ide.sdt.aspects.hydra";
    													
	public static AnalysisCallbackProviderRegistry getInstance() {
		return INSTANCE;
	}

	@Override
	protected String getExtensionPointId() {
	    return ANALYSIS_EXTENSION_POINT;
	}
}