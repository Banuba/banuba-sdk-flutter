#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html.
# Run `pod lib lint banuba_sdk.podspec` to validate before publishing.
#
Pod::Spec.new do |s|
  s.name             = 'banuba_sdk'
  s.version          = '2.1.0'
  s.summary          = 'Banuba SDK for Flutter'
  s.description      = <<-DESC
Banuba SDK for Flutter
                       DESC
  s.homepage         = 'https://www.banuba.com/'
  s.license          = { :file => '../LICENSE' }
  s.author           = { 'Banuba' => 'support@banuba.com' }
  s.source           = { :path => '.' }
  s.source_files = 'Classes/**/*'
  s.dependency 'Flutter'
  
  version = '1.13.0'
  s.dependency 'BanubaSdk', version

  s.resources = "bnb-resources"
  
  s.platform = :ios, '14.0'

  # Flutter.framework does not contain a i386 slice.
  s.pod_target_xcconfig = { 'DEFINES_MODULE' => 'YES', 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'i386' }
  s.swift_version = '5.0'
end
