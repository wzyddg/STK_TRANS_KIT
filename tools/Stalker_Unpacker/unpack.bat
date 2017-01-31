for %%f in (..\*.db*) do (
    converter.exe -unpack -xdb %%f -dir .\unpacked
)

pause