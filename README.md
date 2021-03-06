# Welcome to OSBA in Action

This repository includes examples of how to use Open Service Broker for Azure with both Cloud Foundry and Kubernetes.

If you encounter any issues, please open an [issue](https://github.com/jeremyrickard/osba-in-action/issues/new).

You can also contact me at [Jeremy Rickard](mailto:jeremy.rickard@microsoft.com) or find me on Twitter at [jrrickard](https://twitter.com/jrrickard). I'm also regularly on the [Kubernetes Slack](http://slack.k8s.io/), you can find me there as **@jerickar**


* [Prerequisites](#prerequisites)
* [Setup](#setup)
  * [Create your Azure account](#create-your-azure-account)
  * [Install Azure CLI](#the-azure-cli)
  * [Setup your Subscription](#setup-your-subscription)
  * [Create A Service Principal](#create-a-service-principal)
* [Take a Tour of The OSB API](#take-a-tour-of-the-osb-api)
* [Take A Tour of OSBA](#take-a-tour-of-osba)
* [Use OSBA with Cloud Foundry](#use-osba-with-cloud-foundry)
  * [Install CF Dev](#install-cf-dev)
  * [Run OSBA](#run-osba)
  * [Register OSBA](#register-osba)
  * [Create a CosmosDB Instance with Cloud Foundry](#create-a-cosmosdb-instance-with-cloud-foundry)
  * [Deploy an App](#deploy-an-app)
* [Use OSBA with Kubernetes](#use-osba-with-kubernetes)
  * [Installing the Service Catalog CLI](#install-the-service-catalog-cli)
  * [Install Minikube](#install-minikube)
  * [Install Helm](#install-helm)
  * [Install Service Catalog](#install-service-catalog)
  * [Install Open Service Broker for Azure](#install-open-service-broker-for-azure)
  * [Create a CosmosDB Instance with Kubernetes](#create-a-cosmosdb-instance-with-kubernetes)
  * [Deploy an App on Kubernetes](#deploy-an-app-on-kubernetes)
  * [Advanced Topics](#advanced-topics)

## Prerequisites

1. Git is [installed](https://docs.microsoft.com/en-us/azure/devops/git/install-and-set-up-git)
1. Docker is [installed](https://docs.docker.com/install/#relationship-between-ce-and-ee-code). You'll also want [Docker Compose](https://docs.docker.com/compose/install/).
1. Curl is [installed](https://curl.haxx.se/download.html).
1. A Java JDK is [installed](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html).

## Setup

### Clone this Repository

Several of the steps below will use the contents of this repository. You'll want to clone it locally so that you can follow along.

```
git clone https://github.com/jeremyrickard/osba-in-action.git
cd osba-in-action
```

Once you've cloned it locally, you're ready to move on and setup your environment.

### Create your Azure account

To follow this guide, you will need a Microsoft Azure Account. If you don't already have one, click [this link](https://azure.microsoft.com/en-us/free/) to get started!

### The Azure CLI

You'll also want the Azure CLI.

Install `az` by following the instructions for your operating system.
See the [full installation instructions](https://docs.microsoft.com/en-us/cli/azure/install-azure-cli?view=azure-cli-latest) if yours isn't listed below. You will need az cli version 2.0.37 or greater.

To install on **MacOS**

```console
brew install azure-cli
```

To install on **Windows**

Download and run the [Azure CLI Installer (MSI)](https://aka.ms/InstallAzureCliWindows).

To intall on **Ubuntu 64-bit**

1. Add the azure-cli repo to your sources:
    ```console
    echo "deb [arch=amd64] https://packages.microsoft.com/repos/azure-cli/ wheezy main" | \
         sudo tee /etc/apt/sources.list.d/azure-cli.list
    ```
1. Run the following commands to install the Azure CLI and its dependencies:
    ```console
    sudo apt-key adv --keyserver packages.microsoft.com --recv-keys 52E16F86FEE04B979B07E28DB02C46DF417A0893
    sudo apt-get install apt-transport-https
    sudo apt-get update && sudo apt-get install azure-cli
    ```

### Setup your Subscription

Now that you have the tools installed, let's identify your Azure subscription and save it for later use!

1. Run `az login` and follow the instructions in the command output to authorize `az` to use your account
1. List your Azure subscriptions:
    ```console
    az account list -o table
    ```
1. Copy your subscription ID and save it in an environment variable:

    **Bash**
    ```console
    export AZURE_SUBSCRIPTION_ID="<SubscriptionId>"
    ```

    **PowerShell**
    ```console
    $env:AZURE_SUBSCRIPTION_ID = "<SubscriptionId>"

You will also want to create a resource group. Create one with the az cli using the following command.

```console
az group create --name osba-in-action --location eastus
```

### Create A Service Principal

This creates an identity for Open Service Broker for Azure to use when provisioning
resources on your account.

1. Create a service principal with RBAC enabled:
    ```console
    az ad sp create-for-rbac --name osba-in-action -o table
    ```
1. Save the values from the command output in environment variables:

    **Bash**
    ```console
    export AZURE_TENANT_ID=<Tenant>
    export AZURE_CLIENT_ID=<AppId>
    export AZURE_CLIENT_SECRET=<Password>
    ```

    **PowerShell**
    ```console
    $env:AZURE_TENANT_ID = "<Tenant>"
    $env:AZURE_CLIENT_ID = "<AppId>"
    $env:AZURE_CLIENT_SECRET = "<Password>"
    ```

You now have your environment configured to work with Azure resources and OSBA! Next, let's take a look at OSB and OSBA.

## Take a Tour of The OSB API

The Open Service Broker API is also developed on GitHub. You can find the repository [here](https://github.com/openservicebrokerapi/servicebroker). There are regular developer meetings that you can attend, with all the necessary information found in the GitHub repo.

## Take A Tour of OSBA

Open Service Broker for Azure is an open source project and can be found on GitHub! You can find it at https://github.com/Azure/open-service-broker-azure.

You can run Open Service Broker for Azure locally by running the following command:

```console
docker-compose run -p 8080:8080 broker
```

While this is running, you can use cURL to interact with the broker. To verify that the catalog endpoint is working, run:

```
curl -u username:password http://localhost:8080/v2/catalog --header "X-Broker-API-Version: 2.13"
```

This will return a large blob of JSON that represents the Catalog.

## Use OSBA with Cloud Foundry

To try out OSBA with Cloud Foundry, you'll first need to install Cloud Foundry! There are two ways to get going with CF locally. For both, you'll want to install the [CF CLI](https://pivotal.io/platform/pcf-tutorials/getting-started-with-pivotal-cloud-foundry-dev/install-the-cf-cli).

### Install CF Dev

The first way to install CF locally is to install [PCF Dev](https://pivotal.io/platform/pcf-tutorials/getting-started-with-pivotal-cloud-foundry-dev/install-pcf-dev). This requires an account with Pivotal. You can follow the instructions there to get started. 

The second way is to use the newer open source [CF Dev](https://github.com/cloudfoundry-incubator/cfdev) release. At the time of writing this guide, this only works on Mac OS.

Once you have these installed, you can run `cf dev start` to launch the local CF environment. Depending on your computer, this may take a little time. Once complete, log in to your instance and follow the following steps to register OSBA with your CF instance.

### Run OSBA

Next, start OSBA up. The command above can be reused, but run it as a background process.

```
docker-compose run -p 8080:8080 broker
```

Register OSBA with Cloud Foundry. To do this, you'll need your IP address.

```bash
ifconfig
```

```windows
ipconfig
```

### Register OSBA

Next, use the **cf** cli to register OSBA as a space scoped broker:

```
cf create-service-broker osba username password http://<IP-ADDRESS>:8080 --space-scoped
```

Now, you should be able to view the marketplace

```
cf marketplace
```

There should be a number of Azure services listed. Next, we'll provision an instance of the CosmosDB MongoDB Service.

### Create a CosmosDB Instance with Cloud Foundry

First, verify there is no CosmosDB instance:


```
az cosmosdb list -g osba-in-action -o table
```

Next, create the service

```
cf create-service azure-cosmosdb-mongo-account account cosmosdb -c '{
  "location": "eastus",
  "resourceGroup": "osba-in-action",
  "ipFilters" : {
    "allowedIPRanges" : ["0.0.0.0/0"]
  }
}'
```

Note, this provision action may take some time, so you'll want to check the status with:

```
cf service cosmosdb
```

When it's done, you can verify that it was created with the Azure CLI:

```
az cosmosdb list -g osba-in-action -o table
```

### Deploy an App

Next, we'll push the **spring-music** sample application to your CF instance and bind it to CosmosDB! A copy of the Spring Music app is in this repository and it contains a CF manifest that will bind to your new cosmosdb instance.

```yaml
---
applications:
- name: spring-music
  memory: 1G
  path: build/libs/spring-music.jar
env:
   JBP_CONFIG_SPRING_AUTO_RECONFIGURATION: '[enabled: false]'
   RUNTIME: cf
   SPRING_PROFILES_ACTIVE: cosmosdb
services:
- cosmosdb
```

Now, you're ready to push the app!

```
cd spring-music
./gradlew clean assemble
cf push
```

Once this finishes, you can access the app at **http://http://spring-music.v3.pcfdev.io/**

Deploying an application to CF is as simple as that. Because you've included a service reference in the manifest, the CosmosDB instance will automatically be bound to your new app. You should be able to access it at:


## Use OSBA with Kubernetes

You can use either Minikube or the Azure Kubernetes Service to experiment with OSBA and the OSBA GitHub repository provides great quick-start guides for both! We'll use Minikube here, to show an experience similar to Cloud Foundry. The Kubernetes CLI, **kubectl**, doesn't have great integration for Service Brokers. Instead, you'll want to install the service catalog cli.

### Install the Service Catalog CLI

Follow the appropriate instructions for your operating system to install svcat. The binary
can be used by itself, or as a kubectl plugin.

The snippets below install the latest version of svcat. We also publish binaries for
our canary (master) builds, and tags using the following prefixes:

* Latest release: https://download.svcat.sh/cli/latest
* Tagged releases: https://download.svcat.sh/cli/VERSION
  where `VERSION` is the release, for example `v0.1.20`.
* Canary builds: https://download.svcat.sh/cli/canary
* Previous canary builds: https://download.svcat.sh/cli/VERSION-GITDESCRIBE 
  where `GITDESCRIBE` is the result of calling `git describe --tags`, for example `v0.1.20-1-g203c8ad`.

#### MacOS with Homebrew

```
brew update
brew install kubernetes-service-catalog-client
```

#### MacOS

```
curl -sLO https://download.svcat.sh/cli/latest/darwin/amd64/svcat
chmod +x ./svcat
mv ./svcat /usr/local/bin/
svcat version --client
```

#### Linux

```
curl -sLO https://download.svcat.sh/cli/latest/linux/amd64/svcat
chmod +x ./svcat
mv ./svcat /usr/local/bin/
svcat version --client
```

#### Windows

The snippet below adds a directory to your PATH for the current session only.
You will need to find a permanent location for it and add it to your PATH.

```
iwr 'https://download.svcat.sh/cli/latest/windows/amd64/svcat.exe' -UseBasicParsing -OutFile svcat.exe
mkdir -f ~\bin
$env:PATH += ";${pwd}\bin"
svcat version --client
```

### Create a Cluster

Most of this demonstration can be completed on either a local or remote Kubernetes cluster. You can chose to run locally, with Minikube, or using the Azure Kubernetes Service (AKS). Instructions for both can be found below

#### Create a Minikube cluster

The next step is to install Minikube. Minikube has some[prerequisites](https://kubernetes.io/docs/tasks/tools/install-minikube/) you'll need to install first, specifically you'll need a Hypervisor and the `kubectl` command line tool. The Minikube documentation has links to help you get going on the appropriate system. Once installed, you can create a Minikube cluster with the following command

```
minikube start
```

When you run this command, the cluster will start with RBAC enabled and your kubectl configuration will automatically be updated. You can skip down to [Install Helm](#install-helm) at this point.

#### Create an AKS cluster

To use Service Catalog with AKS, you'll first need to create an AKS cluster. This can be done with the Azure portal or with the Azure CLI. The following Azure CLI command will create a 3 node AKS cluster.

```
az aks create --resource-group osba-in-action --name osba-in-action --generate-ssh-keys
```

This will take some time to fully provision. When it completes, you'll want to get the relevant cluster configuration for your kubectl configuration with the following command:

```
az aks get-credentials --resource-group osba-in-action --name osba-in-action --admin
```

You cluster should be ready to use with the following steps now!

### Install Helm

Next, you'll need to install [Helm](https://github.com/kubernetes/helm). Helm is a tool for installing pre-configured applications on Kubernetes. We'll use it to install Service Catalog, OSBA and our Spring Music app.

Install `helm` by running the following command:

To install on **MacOS**:

```console
brew install kubernetes-helm
```

To install on **Windows**:

1. Download the latest [Helm release](https://storage.googleapis.com/kubernetes-helm/helm-v2.7.2-windows-amd64.tar.gz).
1. Decompress the tar file.
1. Copy **helm.exe** to a directory on your PATH.

To install on **Linux**:

```console
curl https://raw.githubusercontent.com/kubernetes/helm/master/scripts/get | bash
```

Next, deploy Helm to your cluster:

```
kubectl create -f https://raw.githubusercontent.com/Azure/helm-charts/master/docs/prerequisities/helm-rbac-config.yaml
helm init --service-account tiller
```

This may take a few moments, so you can watch the status of Helm by looking for the status of the Tiller pod, which will be in the kube-system namespace:

```
kubectl get pods -n kube-system -w
```

### Install Service Catalog

Once the Helm Tiller pod is running, you can install Service Catalog and OSBA on your cluster. First, install Service Catalog.

```console
helm repo add svc-cat https://svc-catalog-charts.storage.googleapis.com
helm install svc-cat/catalog --name catalog --namespace catalog
```

Again, you'll want to wait until the pods are running before moving on.

```console
$ kubectl get pods --namespace catalog
NAME                                                     READY     STATUS    RESTARTS   AGE
po/catalog-catalog-apiserver-5999465555-9hgwm            2/2       Running   4          9d
po/catalog-catalog-controller-manager-554c758786-f8qvc   1/1       Running   11         9d
```

You can use the `-w` flag as well to wait for the output to change.

### Install Open Service Broker for Azure

Next, install OSBA. Here, you will use the environment variables that were set above, so use the appropriate commands below.

With **Bash**, do the following
```console
helm repo add azure https://kubernetescharts.blob.core.windows.net/azure
helm install azure/open-service-broker-azure --name osba --namespace osba \
  --set azure.subscriptionId=$AZURE_SUBSCRIPTION_ID \
  --set azure.tenantId=$AZURE_TENANT_ID \
  --set azure.clientId=$AZURE_CLIENT_ID \
  --set azure.clientSecret=$AZURE_CLIENT_SECRET \
  --set modules.minStability=experimental
```

On windows, use **PowerShell** to do the following:
```console
helm repo add azure https://kubernetescharts.blob.core.windows.net/azure
helm install azure/open-service-broker-azure --name osba --namespace osba `
  --set azure.subscriptionId=$env:AZURE_SUBSCRIPTION_ID `
  --set azure.tenantId=$env:AZURE_TENANT_ID `
  --set azure.clientId=$env:AZURE_CLIENT_ID `
  --set azure.clientSecret=$env:AZURE_CLIENT_SECRET `
  --set modules.minStability="experimental"
```

After you've run these commands, be sure to check on the status of Open Service Broker for Azure by running the
following command and checking that every pod is in the `Running` state.

You may need to wait a few minutes, rerunning the command until all of the resources are ready.
```console
$ kubectl get pods --namespace osba
NAME                                           READY     STATUS    RESTARTS   AGE
po/osba-azure-service-broker-8495bff484-7ggj6   1/1       Running   0          9d
po/osba-redis-5b44fc9779-hgnck                  1/1       Running   0          9d
```

### Create a CosmosDB Instance with Kubernetes

Now, we are ready to create a **ServiceInstance** using the service catalog cli:

```console
svcat provision cosmosdb --class azure-cosmosdb-mongo-account --plan account  --params-json '{
  "location": "eastus",
  "resourceGroup": "osba-in-action",
  "ipFilters" : {
    "allowedIPRanges" : ["0.0.0.0/0"]
  }
}'
```

Once that completes, you can deploy the application using Helm

```console
helm install ./contrib/kubernetes/charts/spring-music -n spring-music
```

### Deploy an App on Kubernetes

Now we should be able to access our application:

```
$ kubectl get pods
NAME                                         READY     STATUS    RESTARTS   AGE
spring-music-spring-music-7c9d967686-n5zc7   1/1       Running   0          1m
$ kubectl port-forward spring-music-spring-music-7c9d967686-n5zc7 8080:8080
```

Now you can open a web browser and go to http://localhost:8080. You can also verify data has been created in the new instance with the [Azure Portal](https://portal.azure.com)

## Advanced Topics

A new feature in Service Catalog is the ability to register service brokers in specific namespaces. This, when combined with another feature called Catalog Restrictions, enables you to expose very specific services to different teams. You can leverage Kubernetes Role Based Access Control in order to limit who can provision services, and what services they can provision.

To try out these capabilities, please see [OSBA Namespace Broker Demo](https://github.com/jeremyrickard/osba-namespace-broker-demo)

## Cleanup

To remove the resources we have created in Azure, you can run the following command to remove the resource group:

```
az group delete -n osba-in-action
```