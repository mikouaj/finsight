exec { 'yumupdate':
  command => "/usr/bin/yum update -y"
}

package { 'docker':
  require => Exec['yumupdate'],
  ensure => installed
}

exec { 'composedownload':
  command => "curl -L https://github.com/docker/compose/releases/download/1.16.1/docker-compose-`uname -s`-`uname -m` -o /tmp/docker-compose"
}

file { 'composebin':
  require => Exec['composedownload'],
  path => '/usr/local/bin/docker-compose',
  ensure => file,
  owner => 'root',
  group => 'root',
  mode => '755',
  source => '/tmp/docker-compose
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
  require => Package['docker'],
  command => 'docker load -i /tmp/finsight.tar'
}

exec {
  require => [
    File['composefile'],
    File['composebin'],
    Exec['imageimport']
  ],
  cwd => "/home/finsight", 
  command => "docker-compose up -d"
}
