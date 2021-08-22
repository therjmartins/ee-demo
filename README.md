# Deploy Jenkins Using Vagrant and VirtualBox

## Background

This project makes use of a combination of Ansible, Virtualbox and Vagrant to deploy a 
Jenkins Controller and an Agent.

There is an expectation that the host targeted is using the standard Ubuntu 20.04 build that is used in the organization. It is also assumed that this system has direct access to the internet.

It is advised that this project be executed on a host running Ansible 2.9. It is also expected that ssh host key authentication is already configured for to connect as the ```devops``` user on the target host.

## Overview

Once Virtualbox is installed successfully, Vagrant will be installed. Ansible will be installed onto the target system as part of the process to allow its usage as a provisioner within Vagrant. 

Vagrant will create two virtual machines using Virtualbox, a ```controller``` and an ```agent```. Whereafter the Ansible provisioner called within Vagrant will run the ```deploy_jenkins.yml``` playbook to complete the configuration of the virtual machines created. It will install a Jenkins Contoller on the ```controller``` vm, setup an SSH key credential and configure and run a Jenkins Agent on the ```agent``` vm. Some other magic to take care of dependencies and logic also happens throughout the process, however that needn't be discussed in this file since the playbooks are largely self-documenting.

## Usage

Update the inventory file such that the ```demo``` group contains the host which will be targeted for deployment. For instance if the host targeted is ```ee-demo.example.com``` the ```inventory``` file should look like this:

```ini
[demo]
ee-demo.example.com
```

Should your ssh key require a passphrase, make use of the ssh-add command in order to avoid being prompted for the passphrase during playbook execution.
 
```bash
user@host $ ~/project/ ssh-add                                                                 
Enter passphrase for /Users/demouser/.ssh/id_rsa:
Identity added: /Users/demouser/.ssh/id_rsa (/Users/demouser/.ssh/id_rsa)
```

The ```site.yml``` playbook is used to deploy the necessary as described in the ```Background``` section above. When executing the playbook, be sure to pass the ```-K``` option to be prompted for the privilege escalation password since escalation is used in many parts of the automation.

```bash
user@host $ ~/project/  ansible-playbook site.yml -K
BECOME password:
```

The playbook execution will prompt you for the username and password to setup in the Jenkins instance deployed. If you provide empty responses the default username used will be ```admin``` and the default password used will be ```password```.

```bash
Please provide the name for the user to create in Jenkins: admin
Please provide the password to set for the user created in Jenkins:
```

After providing the above info, execution will proceed and upon completion you will be presented with the url which can be used to access your newly deployed Jenkins instance.

```
    Congratulations!! The deployment of Jenkins is now complete.

    You can access the instance using your browser by navigating to:
    http://192.168.1.167:8080
```

Login to Jenkins using either the username and password you provided when prompted, or the defaults as mentioned earlier.


## Teardown

Should you which to remove the virtual machines deployed, and consquently the Jenkins instances, simply execute the ```remove.yml``` playbook.

BE VERY SURE WHEN DOING SO AS THIS IS AN IRRECOVERABLE ACTION

```bash
user@host $ ~/project/  ansible-playbook remove.yml
```

