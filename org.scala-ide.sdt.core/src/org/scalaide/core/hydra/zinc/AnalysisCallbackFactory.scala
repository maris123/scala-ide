package org.scalaide.core.hydra.zinc

import scala.tools.eclipse.contribution.weaving.jdt.hydra.IAnalysisCallbackFactory
import xsbti.compile.analysis.ReadStamps
import xsbti.compile.IncOptions
import sbt.internal.inc.hydra.AnalysisCallback
import java.io.File
import xsbti.compile.Output
import xsbti.api.AnalyzedClass
import scala.collection.Set
import org.scalaide.logging.HasLogger

class AnalysisCallbackFactory extends IAnalysisCallbackFactory with HasLogger {
  
  override def create(internalBinaryToSourceClassName: String => Option[String],
                 internalSourceToClassNamesMap: File => Set[String],
                 externalAPI: (File, String) => Option[AnalyzedClass],
                 stampReader: ReadStamps,
                 output: Output,
                 options: IncOptions): AnalysisCallback = {
    logger.info("IN ANALYSIS CALLBACK FACTORY")
    new AnalysisCallback(internalBinaryToSourceClassName, internalSourceToClassNamesMap, externalAPI, stampReader, output, options)
  }
                      
  override def isHydraInstallation(): Boolean = true
}