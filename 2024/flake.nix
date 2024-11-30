{
    description = "Flake for AoC 2024 done in Python";
    inputs.nixpkgs.url = "github:NixOS/nixpkgs/nixpkgs-unstable";
    inputs.flake-utils.url = "github:numtide/flake-utils";

    outputs = { self, nixpkgs, flake-utils }:
        flake-utils.lib.eachDefaultSystem (system:
            let
                pkgs = nixpkgs.legacyPackages.${system};
            in
            {
                devShells.default = pkgs.mkShell {
                    packages = [
                        pkgs.python312
                        pkgs.python312Packages.pylint
                        pkgs.python312Packages.black
                    ];
                };
            }
        );
}
