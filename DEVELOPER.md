
# Overview
[Red Hat OpenShift Developer CLI (odo)](https://docs.openshift.com/container-platform/4.11/cli_reference/developer_cli_odo/understanding-odo.html) is a tool for creating applications on OpenShift Container Platform and Kubernetes. With odo, you can develop, test, debug, and deploy microservices-based applications on a Kubernetes cluster without having a deep understanding of the platform. 

This document walks you through on how to use odo to deploy and test an application using the RHODA services.

# Prerequisits
1) Install odo on your local machine. See [odo installation](https://docs.openshift.com/container-platform/4.11/cli_reference/developer_cli_odo/installing-odo.html) for installation instructions.
2) RHODA has been deployed in your OpenShift cluster.

# Deploy application and RHODA services
1) Clone git repo for the sample application 
[mongo-quickstart](https://github.com/RHEcosystemAppEng/mongo-quickstart):
```
git clone git@github.com:RHEcosystemAppEng/mongodb-atlas-kubernetes.git
cd mongodb-atlas-kubernetes
```
2) Log on to your OpenShift cluster using an cluster admin user and switch to the `openshift-dbaas-opererator` project.
```
odo login <server URL> --username=<user> --password=<password>
odo project set openshift-dbaas-operator
```
3) Build the application jar file using mvn.
```
./mvnw package
```
4) List the component types availabe in OpenShift Developer Catalog
```
$ odo catalog list components
Odo Devfile Components:
NAME                             DESCRIPTION                                                         REGISTRY
dotnet50                         Stack with .NET 5.0                                                 DefaultDevfileRegistry
dotnet60                         Stack with .NET 6.0                                                 DefaultDevfileRegistry
dotnetcore31                     Stack with .NET Core 3.1                                            DefaultDevfileRegistry
go                               Stack with the latest Go version                                    DefaultDevfileRegistry
java-maven                       Upstream Maven and OpenJDK 11                                       DefaultDevfileRegistry
java-openliberty                 Java application Maven-built stack using the Open Liberty ru...     DefaultDevfileRegistry
java-openliberty-gradle          Java application Gradle-built stack using the Open Liberty r...     DefaultDevfileRegistry
java-quarkus                     Quarkus with Java                                                   DefaultDevfileRegistry
java-springboot                  Spring Boot® using Java                                             DefaultDevfileRegistry
java-vertx                       Upstream Vert.x using Java                                          DefaultDevfileRegistry
java-websphereliberty            Java application Maven-built stack using the WebSphere Liber...     DefaultDevfileRegistry
java-websphereliberty-gradle     Java application Gradle-built stack using the WebSphere Libe...     DefaultDevfileRegistry
java-wildfly                     Upstream WildFly                                                    DefaultDevfileRegistry
java-wildfly-bootable-jar        Java stack with WildFly in bootable Jar mode, OpenJDK 11 and...     DefaultDevfileRegistry
nodejs                           Stack with Node.js 16                                               DefaultDevfileRegistry
nodejs-angular                   Stack with Angular 14                                               DefaultDevfileRegistry
nodejs-nextjs                    Stack with Next.js 12                                               DefaultDevfileRegistry
nodejs-nuxtjs                    Stack with Nuxt.js 2                                                DefaultDevfileRegistry
nodejs-react                     Stack with React 18                                                 DefaultDevfileRegistry
nodejs-svelte                    Stack with Svelte 3                                                 DefaultDevfileRegistry
nodejs-vue                       Stack with Vue 3                                                    DefaultDevfileRegistry
php-laravel                      Stack with Laravel 8                                                DefaultDevfileRegistry
python                           Python Stack with Python 3.9                                        DefaultDevfileRegistry
python-django                    Stack with Django                                                   DefaultDevfileRegistry
```
5) Create an application component for mongo-quickstart, which is a java quakus application.
```
$ odo create java-quarkus mytestcomponent  --app=app
Devfile Object Creation
 ✓  Checking if the devfile for "java-quarkus" exists on available registries [182711ns]
 ✓  Creating a devfile component from registry "DefaultDevfileRegistry" [955ms]
Validation
 ✓  Validating if devfile name is correct [47993ns]
 ✓  Validating the devfile for odo [13ms]
 ✓  Updating the devfile with component name "mytestcomponent" [2ms]

Please use `odo push` command to create the component with source deployed
$ odo list
APP     NAME                PROJECT                      TYPE        STATE      MANAGED BY ODO
app     mytestcomponent     openshift-dbaas-operator     quarkus     Not Pushed     Yes
```
6) Find RHODA service types available in the developer catalog.
```
$ odo catalog list services|grep dbaas-operator|grep DBaaS
dbaas-operator.v0.3.1-dev            DBaaSConnection, DBaaSInstance, DBaaSInventory, DBaaSPlatform, DBaaSPolicy, DBaaSProvider
```
7) Create a provider account service
First create a yaml file `atlas-key-secret.yaml` for an OpenShift secret containing the credentials to access MongoDB Atlas. Note that the data values should be base-64 encoded.
```
$cat atlas-key-secret.yaml
apiVersion: v1
kind: Secret
metadata:
  name: my-atlas-key
  labels:
    atlas.mongodb.com/type:  credentials 
type: Opaque
data:
  orgId: <my org id>
  privateApiKey: <my private API key>
  publicApiKey: <my public API key>
```
Then deploy the secret in the OpenShift cluster.
```
oc apply -f atlas-key-secret.yaml
```
Now create a provider service. Note that you can pass in additional parameters using the `-p` option.
```
odo service create dbaas-operator.v0.3.1-dev/DBaaSInventory myinventory -p credentialsRef.name=my-atlas-key -p providerRef.name=mongodb-atlas-registration  --inlined
```
8) If you do not have the database instance ID which you want the application to connect to, you can deploy the provider account service first.
```
odo push
```
Then you can find the list of DB instances from the status.
```
oc describe dbaasinventory myinventory
```
You can find the instance list like below:
```
  Instances:
    Instance ID:  62f427209e26026741ceb149
    Instance Info:
      Connection Strings Standard Srv:  mongodb+srv://my-atlas-cluster-103.1lubzxs.mongodb.net
      Instance Size Name:               M0
      Project ID:                       62f4271b7da25f6b1b0009f5
      Project Name:                     my-atlas-project-103
      Provider Name:                    AWS
      Region Name:                      US_EAST_1
      State:                            Ready
    Name:                               my-atlas-cluster-103
    Instance ID:                        62f5a5ec7b1dc86ddb91b8ba
    Instance Info:
      Connection Strings Standard Srv:  mongodb+srv://my-atlas-cluster-105.mjdoy2g.mongodb.net
      Instance Size Name:               M0
      Project ID:                       62f59e5d0eae32248575ddc6
      Project Name:                     my-atlas-project-105
      Provider Name:                    AWS
      Region Name:                      US_EAST_1
      State:                            Ready
```
8) Now you can create a connection service.
```
odo service create dbaas-operator.v0.3.1-dev/DBaaSConnection myconnection -p instanceID=<my DB instance ID> -p inventoryRef.name=myinventory -p inventoryRef.namespace=openshift-dbaas-operator   --inlined
```
9) Create a service binding.
```
odo link DBaaSConnection/myconnection --map credentials='path={.myconnection.status.credentialsRef.name},objectType=Secret' --map configuration='path={.myconnection.status.connectionInfoRef.name},objectType=ConfigMap' --bind-as-files --inlined
```
10) Deploy the application, services and service binding to the cluster.
```
odo push
```
11) 
# References
[Understanding odo](https://docs.openshift.com/container-platform/4.11/cli_reference/developer_cli_odo/understanding-odo.html)

