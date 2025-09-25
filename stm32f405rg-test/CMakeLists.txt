cmake_minimum_required(VERSION 3.22)

#
# This file is generated only once,
# and is not re-generated if converter is called multiple times.
#
# User is free to modify the file as much as necessary
#

# Setup compiler settings
set(CMAKE_C_STANDARD 11)
set(CMAKE_C_STANDARD_REQUIRED ON)
set(CMAKE_C_EXTENSIONS ON)


# Define the build type
if(NOT CMAKE_BUILD_TYPE)
    set(CMAKE_BUILD_TYPE "Debug")
endif()

# Set the project name
set(CMAKE_PROJECT_NAME stm32f405rg-test)

# Enable compile command to ease indexing with e.g. clangd
set(CMAKE_EXPORT_COMPILE_COMMANDS TRUE)

# Core project settings
project(${CMAKE_PROJECT_NAME})
message("Build type: " ${CMAKE_BUILD_TYPE})

# Enable CMake support for ASM and C languages
enable_language(C ASM)

# Create an executable object type
add_executable(${CMAKE_PROJECT_NAME})

# Add STM32CubeMX generated sources
add_subdirectory(cmake/stm32cubemx)

# Link directories setup
target_link_directories(${CMAKE_PROJECT_NAME} PRIVATE
    # Add user defined library search paths
)

# Add sources to executable
target_sources(${CMAKE_PROJECT_NAME} PRIVATE
    ../tests/test_sample.c
    ../tests/unity/unity.c
)

# Add include paths
target_include_directories(${CMAKE_PROJECT_NAME} PRIVATE
    ../tests/unity
)

# Add project symbols (macros)
target_compile_definitions(${CMAKE_PROJECT_NAME} PRIVATE
    # Add user defined symbols
)

# Remove wrong libob.a library dependency when using cpp files
list(REMOVE_ITEM CMAKE_C_IMPLICIT_LINK_LIBRARIES ob)

# Add linked libraries
target_link_libraries(${CMAKE_PROJECT_NAME}
    stm32cubemx

    # Add user defined libraries
)

# Project modification to enable semihosting
target_link_options (${CMAKE_PROJECT_NAME} PRIVATE -specs=rdimon.specs -lc -lrdimon)
set_source_files_properties(Core/Src/syscalls.c Core/Src/sysmem.c PROPERTIES HEADER_FILE_ONLY 1 )

# Enable CTest
include(CTest)
enable_testing()

# Register the test with CTest
add_test(NAME unity_sample_test
        COMMAND C:/tools/qemu/qemu-system-arm.exe
        -kernel $<TARGET_FILE:${CMAKE_PROJECT_NAME}>
        -machine olimex-stm32-h405
        -nographic
        -semihosting
        -semihosting-config enable=on,target=native
        -serial mon:stdio
        -kernel $<TARGET_FILE:${CMAKE_PROJECT_NAME}>
)

# Register the test with CTest
add_test(NAME unity_sample_test_hw
        COMMAND C:/tools/openocd-v0.12.0-i686-w64-mingw32/bin/openocd.exe
        -f board/stm32f4discovery.cfg
        -c "init"
        -c "arm semihosting enable"
        -c "arm semihosting_resexit enable"
        -c "program $<TARGET_FILE:${CMAKE_PROJECT_NAME}> reset"
)

set_tests_properties(unity_sample_test_hw
PROPERTIES
        FAIL_REGULAR_EXPRESSION ":FAIL"
        PASS_REGULAR_EXPRESSION ":PASS"
)