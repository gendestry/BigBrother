import { Alert, Button, Container, Group, LoadingOverlay, SimpleGrid, Stack, Text, TextInput, Title } from "@mantine/core";
import { useForm } from "@mantine/form";
import { useState } from "react";
import { supabaseClient } from "../../supabase/supabaseClient";
import { notifications } from "@mantine/notifications";
import { IconInfoCircle } from "@tabler/icons-react";


function Minecraft() {
    const [ loading, setLoading ] = useState(false);

    const form = useForm({
        initialValues: {
            username: "",
            name: "",
            surname: "",
            email: "",
            description: "",
        },
        validate: {
            username: (value) => value.length < 3 && "Username is too short",
            name: (value) => value.length < 3 && "Name is too short",
            surname: (value) => value.length < 3 && "Surname is too short",
            email: (value) => !value.includes("@") && "Invalid email",
        }
    });


    function submitUser({ username, name, surname, email, description }: { username: string, name: string, surname: string, email: string, description: string }) {
        setLoading(true);
        supabaseClient.from("users")
            .insert({ username, name, surname, email, description })
            .then((response) => {
                if(response.error) {
                    notifications.show({ title: "Error adding user", message: response.error.message, color: "red" });
                }
                else {
                    notifications.show({ title: "User added", message: "User added successfully", color: "green" });
                }
                setLoading(false);
            });
    }

    return (
        <Container w="70%">
            <Stack>
                <LoadingOverlay visible={loading} />
                <Title ta="center" order={2}>Minecraft login form</Title>
                <Text ta="center">To register on the server please fill in the following form. 
                    Once submitted I will confirm you (if I know who you are) making it possible for you to join on the server. 
                    If I have any other questions, I will use the specified email address to contact you. 
                </Text>
                <Alert withCloseButton variant="light" color="red" title="Fake submissions" icon={<IconInfoCircle />}>
                    Please use real information. Fake submissions will be deleted and the IP address will be banned from the server.
                </Alert>
                <form onSubmit={form.onSubmit(submitUser)}>
                    <TextInput withAsterisk label="Username" placeholder="Username" {...form.getInputProps("username")} />
                    <SimpleGrid cols={2}>
                        <TextInput withAsterisk label="Name" placeholder="Name" {...form.getInputProps("name")} />
                        <TextInput withAsterisk label="Surname" placeholder="Surname" {...form.getInputProps("surname")} />
                    </SimpleGrid>
                    <TextInput withAsterisk label="Email" placeholder="Email" {...form.getInputProps("email")} />
                    <TextInput label="Description" placeholder="How do I know you" {...form.getInputProps("description")} />
                    <Group mt="md" justify="center">
                        <Button type="submit" variant="filled" disabled={form.isValid() == false}>Submit</Button>
                    </Group>
                </form>
            </Stack>
        </Container>
    );
};

export default Minecraft;
