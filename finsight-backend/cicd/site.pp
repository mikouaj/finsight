exec { 'yumupdate':
  command => "/usr/bin/yum update -y"
}

package { 'docker':
  require => Exec['yumupdate'],
  ensure => installed
}

service { 'docker':
  require => Package['docker'],
  ensure => running,
  enable => true
}

exec { 'composedownload':
  command => '/usr/bin/curl -L https://github.com/docker/compose/releases/download/1.16.1/docker-compose-Linux-x86_64 -o /tmp/docker-compose'
}

file { 'composebin':
  require => Exec['composedownload'],
  path => '/usr/local/bin/docker-compose',
  ensure => file,
  owner => 'root',
  group => 'root',
  mode => '755',
  source => '/tmp/docker-compose'
}
  
user { 'finsight':
  ensure => present,
  uid => '1000',
  gid => '1000',
  shell => '/bin/bash',
  home => '/home/finsight'
}

group { 'finsight':
  ensure => present,
  gid => '1000'
}

file { '/home/finsight':
  ensure => directory,
  owner => 'finsight',
  group => 'finsight',
  mode => '0750'
}

file { 'composefile':
  path => '/home/finsight/docker-compose.yml',
  ensure => file,
  owner => 'finsight',
  group => 'finsight',
  mode => '0640',
  source => 'file:///tmp/docker-compose.yml'
}

exec { 'imageimport':
  require => Service['docker'],
  command => '/usr/bin/docker load -i /tmp/finsight.tar.bz2'
}

exec { 'containerstart':
  require => [
    File['composefile'],
    File['composebin'],
    Exec['imageimport']
  ],
  cwd => '/home/finsight', 
  command => '/usr/local/bin/docker-compose up -d'
}
