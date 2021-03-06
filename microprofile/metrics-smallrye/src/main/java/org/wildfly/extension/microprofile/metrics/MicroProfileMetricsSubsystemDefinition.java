/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2018, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.wildfly.extension.microprofile.metrics;

import java.util.Arrays;
import java.util.Collection;

import org.jboss.as.controller.AttributeDefinition;
import org.jboss.as.controller.PersistentResourceDefinition;
import org.jboss.as.controller.ServiceRemoveStepHandler;
import org.jboss.as.controller.SimpleAttributeDefinitionBuilder;
import org.jboss.as.controller.SimpleResourceDefinition;
import org.jboss.as.controller.StringListAttributeDefinition;
import org.jboss.as.controller.capability.RuntimeCapability;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;
import org.jboss.msc.service.ServiceName;

/**
 * @author <a href="http://jmesnil.net/">Jeff Mesnil</a> (c) 2018 Red Hat inc.
 */
public class MicroProfileMetricsSubsystemDefinition extends PersistentResourceDefinition {

    static final String METRICS_REGISTRATION_CAPABILITY = "org.wildlfy.extension.microprofile.metrics.wildfly-registrar";

    static final String CLIENT_FACTORY_CAPABILITY ="org.wildfly.management.model-controller-client-factory";
    static final String MANAGEMENT_EXECUTOR ="org.wildfly.management.executor";
    static final RuntimeCapability<Void> METRICS_REGISTRATION_RUNTIME_CAPABILITY = RuntimeCapability.Builder.of(METRICS_REGISTRATION_CAPABILITY, MetricsRegistrationService.class)
            .addRequirements(CLIENT_FACTORY_CAPABILITY, MANAGEMENT_EXECUTOR)
            .build();

    public static final ServiceName WILDFLY_REGISTRATION_SERVICE = METRICS_REGISTRATION_RUNTIME_CAPABILITY.getCapabilityServiceName();

    static final String HTTP_EXTENSIBILITY_CAPABILITY = "org.wildfly.management.http.extensible";
    static final RuntimeCapability<Void> HTTP_CONTEXT_CAPABILITY = RuntimeCapability.Builder.of("org.wildfly.extension.microprofile.metrics.http-context", MetricsContextService.class)
            .addRequirements(HTTP_EXTENSIBILITY_CAPABILITY)
            .build();
    static final ServiceName HTTP_CONTEXT_SERVICE = HTTP_CONTEXT_CAPABILITY.getCapabilityServiceName();

    static final AttributeDefinition SECURITY_ENABLED = SimpleAttributeDefinitionBuilder.create("security-enabled", ModelType.BOOLEAN)
            .setDefaultValue(new ModelNode(true))
            .setRequired(false)
            .setRestartAllServices()
            .setAllowExpression(true)
            .build();

    static final StringListAttributeDefinition EXPOSED_SUBSYSTEMS = new StringListAttributeDefinition.Builder("exposed-subsystems")
            .setRequired(false)
            .setRestartAllServices()
            .build();

    static final AttributeDefinition PREFIX = SimpleAttributeDefinitionBuilder.create("prefix", ModelType.STRING)
            .setRequired(false)
            .setRestartAllServices()
            .setAllowExpression(true)
            .build();

    static final AttributeDefinition[] ATTRIBUTES = { SECURITY_ENABLED, EXPOSED_SUBSYSTEMS, PREFIX };

    protected MicroProfileMetricsSubsystemDefinition() {
        super(new SimpleResourceDefinition.Parameters(MicroProfileMetricsExtension.SUBSYSTEM_PATH,
                MicroProfileMetricsExtension.getResourceDescriptionResolver(MicroProfileMetricsExtension.SUBSYSTEM_NAME))
                .setAddHandler(MicroProfileMetricsSubsystemAdd.INSTANCE)
                .setRemoveHandler(new ServiceRemoveStepHandler(MicroProfileMetricsSubsystemAdd.INSTANCE))
                .setCapabilities(METRICS_REGISTRATION_RUNTIME_CAPABILITY, HTTP_CONTEXT_CAPABILITY));
    }

    @Override
    public Collection<AttributeDefinition> getAttributes() {
        return Arrays.asList(ATTRIBUTES);
    }
}
