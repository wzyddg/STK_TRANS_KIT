for %%f in (..\*.db*) do (
    converter.exe -unpack -2947ru %%f -dir .\OGSE0693_unpacked
)

pause