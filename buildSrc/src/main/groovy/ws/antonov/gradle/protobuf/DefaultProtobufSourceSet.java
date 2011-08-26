package ws.antonov.gradle.protobuf;

import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.internal.file.UnionFileTree;
import org.gradle.api.tasks.util.PatternFilterable;
import org.gradle.api.tasks.util.PatternSet;
import org.gradle.api.internal.file.FileResolver;
import org.gradle.api.internal.file.DefaultSourceDirectorySet;
import org.gradle.api.file.FileTree;
import org.gradle.util.ConfigureUtil;
import groovy.lang.Closure;

/**
 * 
 */

public class DefaultProtobufSourceSet implements ProtobufSourceSet {
    private final SourceDirectorySet protobuf;
    private final UnionFileTree allProtobuf;
    private final PatternFilterable protobufPatterns = new PatternSet();

    public DefaultProtobufSourceSet(String displayName, FileResolver fileResolver) {
        protobuf = new DefaultSourceDirectorySet(String.format("%s Protobuf source", displayName), fileResolver);
        protobuf.getFilter().include("**/*.proto");
        protobufPatterns.include("**/*.proto");
        allProtobuf = new UnionFileTree(String.format("%s Protobuf source", displayName), protobuf.matching(protobufPatterns));
    }

    private String protocPath = "protoc";

    public String getProtocPath() {
        return protocPath;
    }

    public void setProtocPath(String protocPath) {
        this.protocPath = protocPath;
    }

    public SourceDirectorySet getProtobuf() {
        return protobuf;
    }

    public PatternFilterable getProtobufSourcePatterns() {
        return protobufPatterns;
    }

    public FileTree getAllProtobuf() {
        return allProtobuf;
    }
}