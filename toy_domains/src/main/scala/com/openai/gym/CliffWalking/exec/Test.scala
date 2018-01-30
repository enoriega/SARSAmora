package com.openai.gym.CliffWalking.exec

import com.openai.gym.CliffWalking._
import com.openai.gym.observation_spaces._
import org.sarsamora.policies.{EpGreedyPolicy, Policy}

import scala.collection.mutable

object Test extends App{
  // Get the name of the policy to run
  val policyPath = args(0)

  // Instantiate an action values' parser for frozen lake actions
  val valueLoader = new CliffWalkingActionsActionValues
  // Load a serialized policy and make it greedy
  val policy = Policy.loadPolicy(policyPath, valueLoader).asInstanceOf[EpGreedyPolicy].makeGreedy

  //val stateValues = policy.values.toStateValues

  // Create a new FrozenLake environment that will run the policy
  val environment = new CliffWalkingEnvironment()

  // Memory of the observed rewards
  val cumulativeRewards = new mutable.ArrayBuffer[Double]()

  // Reward of the episode
  var reward = 0.0

  // Initialize the environment by calling gym's reset
  environment.reset()
  // Draw the board
  environment.render()

  val visitedStates = new mutable.HashSet[Discrete]()

  // Execute the policy until it finishes
  while (!environment.done) {
    // Observe the current state of the environment
    val currentState = environment.observeState.asInstanceOf[Discrete]
    if(!visitedStates.contains(currentState)) {
      visitedStates += currentState
      // Select the action given the current state and the possible actions
      val action = policy.selectAction(currentState, environment.possibleActions)
      // Accumulate the observed reward
      reward += environment.execute(action)
      // Draw the board
      environment.render()
    }
    else{
      println("Loop detected!!")
      environment.done = true
    }
  }

  //println(s"Reward: $reward")
  // Keep track of the cumulative reward
  cumulativeRewards += reward

  // Print the average cumulative reward of this policy
  println(s"Average reward out of 1 trails: ${cumulativeRewards.sum/cumulativeRewards.size}")
}
