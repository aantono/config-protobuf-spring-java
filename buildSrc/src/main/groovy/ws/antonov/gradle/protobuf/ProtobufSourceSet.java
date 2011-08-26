/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ws.antonov.gradle.protobuf;

import groovy.lang.Closure;
import org.gradle.api.file.FileTree;
import org.gradle.api.file.SourceDirectorySet;

/**
 * A {@code ProtobufSourceSetConvention} defines the properties and methods added to a {@link org.gradle.api.tasks.SourceSet} by the {@link
 * ProtobufPlugin}.
 */
public interface ProtobufSourceSet {
    /**
     * Returns the source to be compiled by the Protobuf compiler for this source set.
     *
     * @return The Protobuf source. Never returns null.
     */
    public abstract SourceDirectorySet getProtobuf();

    /**
     * All Protobuf source for this source set.
     *
     * @return the Protobuf source. Never returns null.
     */
    public abstract FileTree getAllProtobuf();

    public abstract String getProtocPath();
}
