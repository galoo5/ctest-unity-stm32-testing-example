cmake_minimum_required(VERSION 3.25)
project(ctest C CXX)

# Enable CTest
include(CTest)
enable_testing()

add_executable(test_sample
        tests/test_sample.c
        tests/unity/unity.c
)

add_executable(test_sample_success
        tests/test_sample_success.c
        tests/unity/unity.c
)

if ("${CMAKE_SYSTEM_PROCESSOR}" STREQUAL "arm")
    #  Bare metal startup & libraries
    add_subdirectory(stm32f405rg-test)

    enable_language(ASM)

    # Files syscalls.c and sysmem.c conflict with Semihosting
    # This prevents them from being compiled by treating them as header-only
    set_source_files_properties(
            ${CMAKE_SOURCE_DIR}/stm32f405rg-test/Core/Src/syscalls.c
            ${CMAKE_SOURCE_DIR}/stm32f405rg-test/Core/Src/sysmem.c
            DIRECTORY stm32f405rg-test
            PROPERTIES HEADER_FILE_ONLY ON)

    target_link_options(test_sample PRIVATE -specs=rdimon.specs -lc -lrdimon)
    target_link_options(test_sample_success PRIVATE -specs=rdimon.specs -lc -lrdimon)

    target_link_libraries(test_sample stm32f405rg-test STM32_Drivers)
    target_link_libraries(test_sample_success stm32f405rg-test STM32_Drivers)

    #Utilities search
    FIND_PROGRAM(QEMU_BINARY
            NAMES qemu-system-arm
            HINTS "C:/tools/qemu/" "C:/Program Files/qemu/")

    FIND_PROGRAM(OPENOCD_BINARY
            NAMES openocd
            HINTS "C:/tools/openocd-v0.12.0-i686-w64-mingw32/bin/" "C:/Program Files/openocd/")

    # Register the qemu tests with CTest
    add_test(NAME unity_sample_test
            COMMAND ${QEMU_BINARY}
            -machine olimex-stm32-h405
            -nographic
            -semihosting
            -semihosting-config enable=on,target=native
            -serial mon:stdio
            -kernel $<TARGET_FILE:test_sample>
    )

    # Register the qemu tests with CTest
    add_test(NAME unity_sample_test_success
            COMMAND ${QEMU_BINARY}
            -machine olimex-stm32-h405
            -nographic
            -semihosting
            -semihosting-config enable=on,target=native
            -serial mon:stdio
            -kernel $<TARGET_FILE:test_sample_success>
    )

    # Register the on-device tests with CTest
    add_test(NAME unity_sample_test_hw
            COMMAND ${OPENOCD_BINARY}
            -f board/stm32f4discovery.cfg
            -c "init"
            -c "arm semihosting enable"
            -c "arm semihosting_resexit enable"
            -c "program $<TARGET_FILE:test_sample> reset"
    )

    add_test(NAME unity_sample_test_success_hw
            COMMAND ${OPENOCD_BINARY}
            -f board/stm32f4discovery.cfg
            -c "init"
            -c "arm semihosting enable"
            -c "arm semihosting_resexit enable"
            -c "program $<TARGET_FILE:test_sample_success> reset"
    )

    set_tests_properties(
            unity_sample_test_hw
            unity_sample_test
            unity_sample_test_success_hw
            unity_sample_test_success
            PROPERTIES
            FAIL_REGULAR_EXPRESSION ":FAIL"
            PASS_REGULAR_EXPRESSION ":PASS"
            TIMEOUT 20
    )

else ()

    # Register the test with CTest
    add_test(NAME devtest-unity_sample_test
            COMMAND test_sample)
    add_test(NAME devtest-unity_sample_test_success
            COMMAND test_sample_success)
endif ()
