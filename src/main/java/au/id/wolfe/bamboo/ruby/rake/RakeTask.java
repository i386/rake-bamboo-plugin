package au.id.wolfe.bamboo.ruby.rake;

import au.id.wolfe.bamboo.ruby.common.BaseRubyTask;
import au.id.wolfe.bamboo.ruby.rvm.RubyLocator;
import au.id.wolfe.bamboo.ruby.rvm.RubyRuntime;
import au.id.wolfe.bamboo.ruby.rvm.RvmLocatorService;
import au.id.wolfe.bamboo.ruby.rvm.RvmUtil;
import com.atlassian.bamboo.configuration.ConfigurationMap;
import com.atlassian.bamboo.process.EnvironmentVariableAccessor;
import com.atlassian.bamboo.process.ProcessService;
import com.atlassian.bamboo.task.TaskContext;
import com.atlassian.bamboo.task.TaskType;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Bamboo task which interfaces with RVM and runs ruby make (rake).
 */
public class RakeTask extends BaseRubyTask implements TaskType {

    @Override
    protected List<String> buildCommandList(String rubyRuntimeName, ConfigurationMap config) {

        final RubyLocator rubyLocator = getRubyLocator();

        final String rakefile = config.get("rakefile");
        final String rakelibdir = config.get("rakelibdir");

        final String targets = config.get("targets");
        Preconditions.checkArgument(targets != null);

        final String bundleExecFlag = config.get("bundleexec");
        final String verboseFlag = config.get("verbose");
        final String traceFlag = config.get("trace");

        final List<String> targetList = RvmUtil.splitRakeTargets(targets);

        final RubyRuntime rubyRuntime = rubyLocator.getRubyRuntime(rubyRuntimeName);

        return new RakeCommandBuilder(rubyLocator, rubyRuntime)
                .addRubyExecutable()
                .addIfBundleExec(bundleExecFlag)
                .addRakeExecutable()
                .addIfRakeFile(rakefile)
                .addIfRakeLibDir(rakelibdir)
                .addIfVerbose(verboseFlag)
                .addIfTrace(traceFlag)
                .addTargets(targetList)
                .build();

    }

    @Override
    protected Map<String, String> buildEnvironment(String rubyRuntimeName, ConfigurationMap config) {

        log.info("Using runtime {}", rubyRuntimeName);

        Preconditions.checkArgument(rubyRuntimeName != null);

        Map<String, String> currentEnvVars = environmentVariableAccessor.getEnvironment();

        return getRubyLocator().buildEnv(rubyRuntimeName, currentEnvVars);
    }


}
