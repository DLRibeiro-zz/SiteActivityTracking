# SiteActivityTracking

This project is the implementation of the **EventSystem**.
It was developed using the [Dropwizard](https://www.dropwizard.io/1.3.8/docs/) framework.
The REST API accepts Protocol Buffer version 3 payload, as described in by [Google Documentation](https://developers.google.com/protocol-buffers/)


##How to build

To build the project, on the project main folder

`gradlew build`

To test the project run

`gradlew test`

To generate the .jar files run 

    gradlew jar
    gradlew senderJar

##Configure Project

To configure the project, in the **config** folder within, there are 4 *.aml* files.

The app_config.yml file is responsible for the configuration for development purposes. 

The other 3 files listed bellow are used for configuring the Docker images.
 - click_config.yml
 - view_config.yml
 - impression_config.yml

Each file contains properties which are used for the servers

1. appName : This can be set for any name, by default the name is set to eventSystem
2. eventType : This property is responsible to set which PATH is set for the API, and which EventType the server will be handled.
It must be set beetween these values {**CLICK**, **VIEW**, **IMPRESSION**}
3. operationLimit : This property is set to limit the amount of operations of each user on each website.

##Run on Minikube

To run this project in containers and in a Kubernetes Node locally, use [Minikube](https://kubernetes.io/docs/setup/minikube/).

Minikube does not offer support for Load-Balancing servers. So each deployment only features a single Pod to handle each type of Event.

After installing **Minikube** run the following commands on the project main folder.

    minikube start --insecure-registry localhost:5000

The flag insecure-registry is to enable for the VM running inside Minikube the access the Docker local registry(described down on the text) for the images built.

The following command makes that Docker related commands are run within the Docker environment of the Minikube VM.

    eval $(minikube docker-env)

After this command, set the local registry, built the images and publish them to local registry

    docker run -d -p 5000:5000 --restart=always --name registry registry:2  
    docker-compose build
    docker tag clickservice localhost:5000/clickservice
    docker tag viewservice localhost:5000/viewservice
    docker tag impressionservice localhost:5000/impressionservice
    docker push localhost:5000/clickservice
    docker push localhost:5000/viewservice
    docker push localhost:5000/impressionservice
    
After the images are published to the local Docker registry, you can use these commands to create Deployments and Services on Minikube

    kubectl create clickservice-service.yaml
    kubectl create viewservice-service.yaml
    kubectl create impressionservice-service.yaml
    kubectl create clickservice-deployment.yaml
    kubectl create viewservice-deployment.yaml
    kubectl create impressionservice-deployment.yaml
    
Now you can check that the pods, services and deployments are running using these commands
     
     kubectl get services
     kubectl get pods
     kubectl get deployments
 
The service is now running, but to be able to send a request to it, get the URL to access the service
 
     minikube service clickservice --url
     minikube service viewservice --url
     minikube service impressionservice --url
     
 These commands will return the URL to access each service.
 
##Testing

After the services are exposed through Minikube, you can run the sender.jar with the parameters to test the API.

     java -jar sender.jar <URL>/<EVENT_TYPE lower case> user www.website.com <EVENT_CODE>
     *EVENT_TYPE : click, view, impression
     *EVENT_Code : click -> 0, view -> 1, impression -> 2


