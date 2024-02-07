#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html.
# Run `pod lib lint banuba_sdk.podspec` to validate before publishing.
#
Pod::Spec.new do |s|
  s.name             = 'banuba_sdk'
  s.version          = '1.9.8'
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
  
  version = '1.9.1'
  s.dependency 'BNBSdkCore', version
  s.dependency 'BNBSdkApi', version
  s.dependency 'BNBEffectPlayer', version
  s.dependency 'BNBFaceTracker', version
  s.dependency 'BNBLips', version
  s.dependency 'BNBSkin', version
  s.dependency 'BNBBackground', version

# Comment below dependencies to significantly reduce the size
  s.dependency 'BNBScripting', version
  s.dependency 'BNBFaceTrackerLite', version
  s.dependency 'BNBHair', version
  s.dependency 'BNBHands', version
  s.dependency 'BNBWatch', version
  s.dependency 'BNBOcclusion', version
  s.dependency 'BNBEyes', version
  s.dependency 'BNBBody', version
  s.dependency 'BNBAcneEyebagsRemoval', version
  s.dependency 'BNBNeurobeautyMakeup', version

  s.resources = "bnb-resources"
  
  s.platform = :ios, '14.0'

  # Flutter.framework does not contain a i386 slice.
  s.pod_target_xcconfig = { 'DEFINES_MODULE' => 'YES', 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'i386' }
  s.swift_version = '5.0'
end
