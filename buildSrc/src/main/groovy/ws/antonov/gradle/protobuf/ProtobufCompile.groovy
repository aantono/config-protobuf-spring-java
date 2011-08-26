package ws.antonov.gradle.protobuf;

import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.compile.Compile;
import org.gradle.api.file.FileCollection
import org.gradle.api.plugins.Convention
import org.gradle.api.internal.DynamicObjectAware
import org.gradle.api.tasks.SourceSet
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.util.GUtil
import org.gradle.api.logging.LogLevel
import org.gradle.api.InvalidUserDataException;

/**
 *
 */

public class ProtobufCompile extends Compile {

    public String getProtocPath() {
        return null
    }

    protected void compile() {
        //println "Compiling protos..."
        //println "${sourceSets.main.java.srcDirs}"
        //println project.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME).protobuf.class
        getDestinationDir().mkdir()
        def dirs = GUtil.join(getDefaultSource().srcDirs, " -I")
        //println dirs
        def files = GUtil.join(getDefaultSource().getFiles(), " ")
        def cmd = "${getProtocPath()} -I${dirs} --java_out=${getDestinationDir()} ${files}"
        logger.log(LogLevel.INFO, cmd)
        Process result = cmd.execute()
        result.waitFor()
        def sbout = new StringBuffer()
        def sberr = new StringBuffer()
        result.consumeProcessOutput(sbout, sberr)
        if (result.exitValue() == 0) {
            logger.log(LogLevel.INFO, sbout.toString())
        } else {
            //logger.log(LogLevel.ERROR, sberr.toString())
            throw new InvalidUserDataException(sberr.toString())
        }
    }
}