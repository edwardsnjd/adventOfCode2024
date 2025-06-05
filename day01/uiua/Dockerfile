# Docker file to define dev environment with uiua binaries installed via cargo
FROM rust:latest
RUN cargo install uiua
ENTRYPOINT ["/usr/local/cargo/bin/uiua"]
