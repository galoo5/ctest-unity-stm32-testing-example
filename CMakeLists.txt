cmake_minimum_required(VERSION 4.0)
project(ctest C CXX)

# Enable CTest
include(CTest)
enable_testing()

# Unity-based minimal test
add_executable(test_sample
    tests/test_sample.c
    tests/unity/unity.c
)

# Register the test with CTest
add_test(NAME unity_sample_test
         COMMAND test_sample)
