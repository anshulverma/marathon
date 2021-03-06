package mesosphere.marathon.core.launchqueue

import mesosphere.marathon.core.launchqueue.LaunchQueue.QueuedTaskCount
import mesosphere.marathon.state.{ AppDefinition, PathId, Timestamp }

import scala.collection.immutable.Seq

object LaunchQueue {

  /**
    * @param app the currently used app definition
    * @param tasksLeftToLaunch the tasks that still have to be launched
    * @param taskLaunchesInFlight the number of tasks which have been requested to be launched
    *                             but are unconfirmed yet
    * @param tasksLaunchedOrRunning the number of tasks which are running or at least have been confirmed to be
    *                               launched
    */
  protected[marathon] case class QueuedTaskCount(
      app: AppDefinition,
      tasksLeftToLaunch: Int,
      taskLaunchesInFlight: Int,
      tasksLaunchedOrRunning: Int,
      backOffUntil: Timestamp) {
    def waiting: Boolean = tasksLeftToLaunch != 0 || taskLaunchesInFlight != 0
  }
}

/**
  * The LaunchQueue contains requests to launch new tasks for an application.
  */
trait LaunchQueue {

  /** Returns all entries of the queue. */
  def list: Seq[QueuedTaskCount]
  /** Returns all apps for which queue entries exist. */
  def listApps: Seq[AppDefinition]

  /** Request to launch `count` additional tasks conforming to the given app definition. */
  def add(app: AppDefinition, count: Int = 1): Unit
  /** Return how many tasks are still to be launched for this PathId. */
  def count(appId: PathId): Int
  /** Remove all task launch requests for the given PathId from this queue. */
  def purge(appId: PathId): Unit

  /** Reset the backoff delay for the given AppDefinition. */
  def resetDelay(app: AppDefinition): Unit
}
