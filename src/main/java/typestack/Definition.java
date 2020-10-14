package typestack;

interface Definition extends TypeResolver {

    Executable executable();

    public static Definition of(TypeResolver resolver, Executable executable) {
        return new Definition() {

            @Override
            public FunctionType type(FunctionType previous) {
                return resolver.type(previous);
            }

            @Override
            public Executable executable() {
                return executable;
            }

        };
    }

}
