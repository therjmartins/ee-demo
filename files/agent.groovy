#!groovy

import hudson.model.*
import jenkins.model.*
import hudson.slaves.*
import hudson.slaves.EnvironmentVariablesNodeProperty.Entry

import hudson.plugins.sshslaves.verifiers.*

// Set a relaxed host key verification strategy
SshHostKeyVerificationStrategy hostKeyVerificationStrategy = new NonVerifyingKeyVerificationStrategy()
  

// Define a "Launch method": "Launch agents via SSH"
ComputerLauncher launcher = new hudson.plugins.sshslaves.SSHLauncher(
        "agent", // Host
        22, // Port
        "e3170776-6144-4036-b80b-68365f849dd6", // Credentials
        (String)null, // JVM Options
        (String)null, // JavaPath
        (String)null, // Prefix Start Agent Command
        (String)null, // Suffix Start Agent Command
        (Integer)null, // Connection Timeout in Seconds
        (Integer)null, // Maximum Number of Retries
        (Integer)null, // The number of seconds to wait between retries
        hostKeyVerificationStrategy // Host Key Verification Strategy
)
// Define a "Permanent Agent"
Slave agent = new DumbSlave(
        "agent",
        "/home/jenkins",
        launcher)
agent.nodeDescription = "Agent 1"
agent.numExecutors = 5
agent.labelString = "linux-agent"
agent.mode = Node.Mode.NORMAL
agent.retentionStrategy = new RetentionStrategy.Always()

List<Entry> env = new ArrayList<Entry>();
env.add(new Entry("key1","value1"))
env.add(new Entry("key2","value2"))
EnvironmentVariablesNodeProperty envPro = new EnvironmentVariablesNodeProperty(env);

agent.getNodeProperties().add(envPro)

// Create a "Permanent Agent"
Jenkins.instance.addNode(agent)

return "Node has been created successfully."