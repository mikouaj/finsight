exec { 'yumupdate':
  command => "/usr/bin/yum update -y"
}

package { 'java-1.8.0-openjdk':
  require => Exec['yumupdate'],
  ensure => installed
}

user { 'finsight':
  require => Group['finsight'],
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
  require => [
    User['finsight'],
    Group['finsight']
  ],
  ensure => directory,
  owner => 'finsight',
  group => 'finsight',    
  mode => '0750'
}

file { '/home/finsight/finsight-backend.jar':
  ensure => file,
  owner => 'finsight',
  group => 'finsight',
  mode => '0640',
  source => 'file:///tmp/finsight-backend.jar'
}

file { '/home/finsight/finsight-backend.yml':
  ensure => file,
  owner => 'finsight',
  group => 'finsight',
  mode => '0640',
  source => 'file:///tmp/finsight-backend.yml'
}                                              

