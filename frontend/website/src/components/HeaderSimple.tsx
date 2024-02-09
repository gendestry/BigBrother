import { useEffect, useState } from 'react';
import { Container, Group, Burger } from '@mantine/core';
import { useDisclosure } from '@mantine/hooks';
import classes from "./HeaderSimple.module.css";
import { router } from '../router/router';

const links = [
  { link: '/minecraft', label: 'Minecraft' },
  { link: '/contact', label: 'Contact' },
  { link: '/github', label: 'GitHub' },
];

export function HeaderSimple() {
  const [opened, { toggle }] = useDisclosure(false);
  const [active, setActive] = useState('/'); 
  

  useEffect(() => {
    setActive(router.state.location.pathname);
  }, []);
  // console.log(router);

  const items = links.map((link) => (
    <a
      key={link.label}
      href={link.link}
      className={classes.link}
      data-active={active === link.link || undefined}
      onClick={(event) => {
        // event.preventDefault();
        setActive(link.link);
      }}
    >
      {link.label}
    </a>
  ));

  return (
    <header className={classes.header}>
      <Container size="md" className={classes.inner}>
        <a href='/' className={classes.link} data-main-button={true} data-active={active === "/" || undefined} onClick={(event) => {setActive('/')}}>oberstar.eu.org</a>
        <Group gap={5} visibleFrom="xs">
          {items}
        </Group>

        <Burger opened={opened} onClick={toggle} hiddenFrom="xs" size="sm" />
      </Container>
    </header>
  );
}