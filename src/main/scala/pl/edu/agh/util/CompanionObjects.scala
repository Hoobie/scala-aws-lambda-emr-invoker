package pl.edu.agh.util

import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduceClient
import com.amazonaws.services.elasticmapreduce.model._

import scala.collection.JavaConverters._


object AmazonElasticMapReduceClient {
  def apply() = new AmazonElasticMapReduceClient()
}

object BootstrapActionConfig {
  def apply(name: String, path: String, args: List[String]) = {
    val script: ScriptBootstrapActionConfig = new ScriptBootstrapActionConfig()
      .withPath(path)
      .withArgs(args.asJava)

    new BootstrapActionConfig()
      .withName(name)
      .withScriptBootstrapAction(script)
  }
}

object StepConfig {
  def apply(name: String, command: String, args: List[String]) = {
    val step: HadoopJarStepConfig = new HadoopJarStepConfig()
      .withJar(command)
      .withArgs(args.asJava)

    new StepConfig()
      .withName(name)
      .withActionOnFailure(ActionOnFailure.TERMINATE_CLUSTER)
      .withHadoopJarStep(step)
  }
}

object AddJobFlowStepsRequest {
  def apply(jobFlowId: String, steps: List[StepConfig]) = {
    new AddJobFlowStepsRequest()
      .withJobFlowId(jobFlowId)
      .withSteps(steps.asJava)
  }
}

object RunJobFlowRequest {
  val emrRelease = "emr-4.6.0"
  val jobName = "EMR Job"
  val loggingURI = "s3://slojewsk/logs"
  val visibleToAllUsers = true

  def apply(bootstrapActions: List[BootstrapActionConfig]) = {
    val applications: List[Application] = List(Application("Hadoop"), Application("Spark"))

    new RunJobFlowRequest()
      .withName(jobName)
      .withReleaseLabel(emrRelease)
      .withApplications(applications.asJava)
      .withInstances(JobFlowInstancesConfig())
      .withLogUri(loggingURI)
      .withVisibleToAllUsers(visibleToAllUsers)
      .withServiceRole("EMR_DefaultRole")
      .withJobFlowRole("EMR_EC2_DefaultRole")
      .withBootstrapActions(bootstrapActions.asJava)
  }
}

object Application {
  def apply(name: String) = new Application()
    .withName(name)
}

object JobFlowInstancesConfig {
  val instanceCount = 2
  val instanceType = "m3.xlarge"

  def apply() = {
    new JobFlowInstancesConfig()
      .withInstanceCount(instanceCount)
      .withMasterInstanceType(instanceType)
      .withSlaveInstanceType(instanceType)
  }
}

object DescribeClusterRequest {
  def apply(jobFlowId: String) = new DescribeClusterRequest()
    .withClusterId(jobFlowId)
}
