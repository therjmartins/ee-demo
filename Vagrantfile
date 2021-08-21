# encoding: utf-8
# -*- mode: ruby -*-
# vi: set ft=ruby :
# Box / OS
require 'net/ssh'
require 'yaml'
required_plugins = %w(
  vagrant-hostmanager
  vagrant-sshfs
)

VAGRANT_BOX = "ubuntu/focal64"

def ip(n)
  "#{IP_PREFIX}.#{n}"
end

IP_PREFIX="192.168.10" # vbox virtual bridge

# Setup variables for VM build

GB = 1024
CONTROLLER_MEM = 2*GB
AGENT_MEM = 1*GB
MACHINES = [
  { name: "controller", ip: ip(101), primary: true, cpus: 4, mem: CONTROLLER_MEM, add_disk: false },
  { name: "agent", ip: ip(111), primary: false, cpus: 2, mem: AGENT_MEM, add_disk: false },
]
ANSIBLE_VARS="ansible/inventory/vars.yml"


Vagrant.configure("2") do |config|
  config.vm.box = VAGRANT_BOX
  config.vm.synced_folder ".", "/vagrant", type: "sshfs"

  # vagrant-hostmanager: Configure /etc/hosts in machines so that they can look each other up
  config.hostmanager.enabled = true
  config.hostmanager.manage_guest = true
  config.hostmanager.include_offline = true


  MACHINES.each do |m|
    node = m[:name]
    config.vm.define node, primary: m[:primary] do |c|
      c.vm.hostname = "#{node}.example.com"
      c.vm.network :private_network, :ip => m[:ip], virtualbox_intnet: "ee-demo"
      c.vm.network :public_network, 
      c.hostmanager.aliases = [node]

      c.vm.provider "virtualbox" do |vb|
        vb.cpus = m[:cpus]
        vb.memory = m[:mem]
        vb.name = m[:name]
        vb.customize ["modifyvm", :id, "--natdnshostresolver1", "on"]
        add_disk vb, c.vm.hostname, m[:size], 1, 0 if m[:add_disk]
        add_disk vb, c.vm.hostname, m[:size], 1, 1 if m[:add_disk]
      end

    end
  end
end

def generate_host_vars
  map = Hash.new

  map["all"] = { "hosts" => {}}

  MACHINES.each do |m|
    map["all"]["hosts"][m[:name]] = { "node_ip" => m[:ip]}
  end

  File.open(ANSIBLE_VARS, "w") { |f| f.write(map.to_yaml) }
end

def install_plugins(plugins)
  needs_restart = false
  plugins.each do |plugin|
    unless Vagrant.has_plugin? plugin
      system "vagrant plugin install #{plugin}"
      needs_restart = true
    end
  end

  if needs_restart
    exec "vagrant #{ARGV.join' '}"
  end
end

install_plugins required_plugins

generate_host_vars
