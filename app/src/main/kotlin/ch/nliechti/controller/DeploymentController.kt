package ch.nliechti.controller

import ch.nliechti.Repository
import ch.nliechti.kubernetesModels.TBZ_DEPLOYMENT_LABEL
import ch.nliechti.kubernetesModels.TBZ_REPLACE_ENV
import ch.nliechti.repository.GithubRepoRepository
import ch.nliechti.repository.KubernetesRepository
import ch.nliechti.repository.KubernetesRepository.getAllDeploymentsInNamespace
import ch.nliechti.service.DeploymentKubernetesService
import ch.nliechti.util.DeploymentUtil
import io.fabric8.kubernetes.api.model.*
import io.fabric8.kubernetes.api.model.apps.Deployment
import io.javalin.http.Context

object DeploymentController {

    fun getDeployment(ctx: Context) {
        val deploymentName: String = ctx.pathParam("deployment-name")
        val namespaces: List<Namespace> = KubernetesRepository.client.namespaces().withLabel(TBZ_DEPLOYMENT_LABEL, deploymentName).list().items
        val deployments = mutableListOf<DeploymentResponse>()
        var totalReady = 0
        var totalDeployments = 0
        namespaces.forEach { namespace ->
            val deploymentsInNamespace = getAllDeploymentsInNamespace(namespace.metadata.name)
            val state = getDeploymentState(deploymentsInNamespace)
            totalReady += state.ready
            totalDeployments += state.total

            deployments.add(DeploymentResponse(
                    getExternalAccess(namespace),
                    getClusterAccess(namespace),
                    getAllReplacesEnv(deploymentsInNamespace),
                    state))
        }

        ctx.json(DeploymentsResponse(deployments, totalReady, totalDeployments))
    }

    private fun getClusterAccess(namespace: Namespace): List<ClusterAccess> {
        val services = KubernetesRepository.getAllServicesInNamespace(namespace.metadata.name)
        return services
                .filter { service -> service.spec.clusterIP != "None" }
                .map { service ->
                    ClusterAccess(service.spec.clusterIP, service.spec.ports.map { port -> port.nodePort })
                }
    }

    private fun getDeploymentState(deployments: List<Deployment>): DeploymentState {
        var ready = 0
        deployments.forEach {
            if (it.status.availableReplicas == 1) ready++
        }
        return DeploymentState(ready, deployments.size)
    }

    private fun getExternalAccess(namespace: Namespace): List<ExternalAccess> {
        val loadBalancer = KubernetesRepository.getAllLoadBalancerInNamespace(namespace.metadata.name)
        return loadBalancer.map { lb ->
            ExternalAccess(lb.status.loadBalancer.ingress.getOrNull(0)?.ip
                    ?: "", lb.spec.ports.map { port -> port.port })
        }
    }

    private fun getAllReplacesEnv(deployments: List<Deployment>): List<EnvVar> {
        return DeploymentKubernetesService.getAllReplacableEnvs(deployments)
    }


    data class DeploymentResponse(val externalAccess: List<ExternalAccess>, val clusterAccess: List<ClusterAccess>, val replacedEnvs: List<EnvVar>, val state: DeploymentState)
    data class DeploymentsResponse(val deployments: List<DeploymentResponse>, val totalReady: Number, val totalDeployments: Number)
    data class ExternalAccess(val ip: String, val ports: List<Int>)
    data class ClusterAccess(val ip: String, val ports: List<Int>)
    data class DeploymentState(val ready: Int, val total: Int)

    fun addDeployment(ctx: Context) {
        val deploymentPost = ctx.body<DeploymentPost>()
        deploymentPost.deployment.name = deploymentPost.deployment.name.toLowerCase()
        val repo = GithubRepoRepository.getGithubRepo(deploymentPost.repositoryId)
        repo?.let {
            DeploymentKubernetesService.createKubernetesConfig(repo, deploymentPost)
        } ?: ctx.res.sendError(400, "No repository with id ${deploymentPost.repositoryId} found")
    }


    data class DeploymentPost(val deployment: ch.nliechti.Deployment, val repositoryId: String, val schoolClassName: String)

}