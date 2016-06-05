package pl.edu.agh

import com.typesafe.scalalogging.LazyLogging
import pl.edu.agh.util._

class EMRInvoker extends LazyLogging {

  val commandRunnerJar: String = "command-runner.jar"

  def lambda() = {
    val emr = AmazonElasticMapReduceClient()

    // bootstrap config
    val bootstrapActionConfig = BootstrapActionConfig("S3 Copy", "file:///usr/bin/aws",
      List("s3", "cp", "s3://us-east-1.elasticmapreduce.samples/flightdata/sparkapp/flightsample_2.10-1.3.jar", "/mnt/"))

    // run job flow
    val runJobFlowRequest = RunJobFlowRequest(List(bootstrapActionConfig))
    val runRequest = emr.runJobFlow(runJobFlowRequest)

    val jobFlowId = runRequest.getJobFlowId
    logger.info(s"Created cluster with job flow id = $jobFlowId")

    // create steps
    val sparkStep = StepConfig("Spark Step", commandRunnerJar,
      List("spark-submit", "--deploy-mode", "client", "--class", "FlightSample", "/mnt/flightsample_2.10-1.3.jar", "s3://slojewsk/output"))

    val req = AddJobFlowStepsRequest(jobFlowId, List(sparkStep))
    emr.addJobFlowSteps(req)

    // get state
    if (jobFlowId != null) {
      val describeClusterRequest = DescribeClusterRequest(jobFlowId)
      val state = emr.describeCluster(describeClusterRequest).getCluster.getStatus.getState
      logger.info(s"Current state of cluster is $state")
    }

    runRequest
  }
}
