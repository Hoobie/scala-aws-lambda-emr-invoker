package pl.edu.agh

import awscala.emr._
import com.typesafe.scalalogging.LazyLogging

/**
  * DO NOT USE!
  */
class ObsoleteEMRInvoker extends LazyLogging {
  implicit val emr = EMR.at(awscala.Region.US_EAST_1)

  val masterInstanceType = "m3.xlarge"
  val masterMarketType = "ON_DEMAND"
  val masterBidPrice = "0.00"
  val coreInstanceType = "m3.xlarge"
  val coreInstanceCount = 1
  val coreMarketType = "ON_DEMAND"
  val coreBidPrice = "0.00"
  val taskInstanceType = "m3.xlarge"
  val taskInstanceCount = 1
  val taskMarketType = "ON_DEMAND"
  val taskBidPrice = "0.00"
  val ec2KeyName = ""
  val hadoopVersion = "2.4.0"

  // job settings
  val jobName = "EMR Job"
  val amiVersion = "3.11.0"
  val loggingURI = "s3://slojewsk/logs"
  val visibleToAllUsers = true

  def lambda() = {
    // individual steps information
    val step1 = emr.jarStep("step1", "jarStep", "command-runner.jar", "",
      List("hdfs dfs -get s3://us-east-1.elasticmapreduce.samples/flightdata/sparkapp/flightsample_2.10-1.3.jar /mnt/"))
    val step2 = emr.jarStep("step2", "jarStep", "command-runner.jar", "",
      List("spark-submit --deploy-mode client --class FlightSample /mnt/flightsample_2.10-1.3.jar s3://slojewsk/output"))
    val steps = List(step1, step2)
    val jobFlowInstancesConfig = emr.buildJobFlowInstancesConfig(masterInstanceType, masterMarketType, masterBidPrice,
      coreInstanceType, coreInstanceCount, coreMarketType, coreBidPrice, taskInstanceType, taskInstanceCount,
      taskMarketType, taskBidPrice, ec2KeyName, hadoopVersion)

    // add steps to new server
    val jobFlowStepsRequest = emr.buildJobFlowStepsRequest(steps)
    val runJobFlowRequest = emr.buildRunRequest(jobName, amiVersion, loggingURI, visibleToAllUsers,
      jobFlowInstancesConfig, jobFlowStepsRequest)
    runJobFlowRequest.setServiceRole("EMR_DefaultRole")
    runJobFlowRequest.setJobFlowRole("EMR_EC2_DefaultRole")
    val runRequest = emr.runJobFlow(runJobFlowRequest)

    val jobFlowId = runRequest.getJobFlowId
    logger.info(s"Created cluster with job flow id = $jobFlowId")
    val state = emr.getClusterState(jobFlowId)
    logger.info(s"Current state of cluster is $state")

    runRequest
  }
}
