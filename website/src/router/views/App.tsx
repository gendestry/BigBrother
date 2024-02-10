import { Container, Stack } from "@mantine/core";
import { HeaderSimple } from "../../components/HeaderSimple";
import { Outlet } from "react-router-dom";

function App () {
    return (
        <Stack>
            <HeaderSimple />
            <Container w="100%">
                <Outlet />
            </Container>
        </Stack>
    );
};

export default App;